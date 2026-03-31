package com.skillsync.session.service;

import com.skillsync.session.client.MentorClient;
import com.skillsync.session.dto.SessionRequestDTO;
import com.skillsync.session.dto.SessionResponse;
import com.skillsync.session.entity.Session;
import com.skillsync.session.entity.SessionStatus;
import com.skillsync.session.event.SessionEvent;
import com.skillsync.session.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/*
 * ================================================================
 * AUTHOR: Ravindranath Potturu
 * CLASS: SessionService
 * DESCRIPTION:
 * Service handling session lifecycle logic including slot creation,
 * booking requests, approvals, cancellations, completion, and publishing
 * messaging events for async processing.
 * ================================================================
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SessionService {

    private final SessionRepository repository;
    private final MentorClient mentorClient;
    private final RabbitTemplate rabbitTemplate;

    /* ================================================================
     * METHOD: createSlot
     * DESCRIPTION:
     * Mentors use this to create an available time slot. Validates 
     * mentor identity, slot duration, future timing, and overlaps.
     * ================================================================ */
    // ⭐ MENTOR CREATES SLOT
    public Session createSlot(SessionRequestDTO dto, Long userId) {

        // ⭐ fetch mentor profile id
        Long mentorId = mentorClient.getMentorProfileId(userId);
        log.info("Resolved the mentor id of user id {} as: {}", userId, mentorId);
        
        if (mentorId == null) {
            log.error("Mentor profile not found or not approved for user id: {}", userId);
            throw new RuntimeException("Mentor profile not found or not approved");
        }

        if (dto.getDurationMinutes() == null || dto.getDurationMinutes() < 10) {
            throw new RuntimeException("Minimum session duration must be 10 minutes");
        }

        if (dto.getDurationMinutes() > 240) {
            throw new RuntimeException("Session duration cannot exceed 4 hours");
        }

        if (dto.getSessionTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Cannot create slot in past time");
        }

        LocalDateTime newStart = dto.getSessionTime();
        LocalDateTime newEnd = newStart.plusMinutes(dto.getDurationMinutes());

        List<Session> mentorSessions = repository.findByMentorId(mentorId);

        for (Session existing : mentorSessions) {

            if (existing.getStatus() == SessionStatus.CANCELLED ||
                existing.getStatus() == SessionStatus.REJECTED) {
                continue;
            }

            LocalDateTime existingStart = existing.getSessionTime();
            LocalDateTime existingEnd =
                    existingStart.plusMinutes(existing.getDurationMinutes());

            boolean overlap =
                    newStart.isBefore(existingEnd) &&
                    newEnd.isAfter(existingStart);

            if (overlap) {
                throw new RuntimeException(
                        "Slot overlaps with another scheduled session"
                );
            }
        }

        Session session = Session.builder()
                .mentorId(mentorId)
                .sessionTime(dto.getSessionTime())
                .durationMinutes(dto.getDurationMinutes())
                .status(SessionStatus.AVAILABLE)
                .build();

        return repository.save(session);
    }

    /* ================================================================
     * METHOD: requestSlot
     * DESCRIPTION:
     * Learners request an available slot. This updates the status 
     * to REQUESTED and fires a RabbitMQ event.
     * ================================================================ */
    // ⭐ LEARNER REQUESTS SLOT
    public Session requestSlot(Long sessionId, Long learnerId) {

        Session session = repository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session slot not found"));

        if (session.getStatus() != SessionStatus.AVAILABLE) {
            throw new RuntimeException("Slot not available for booking");
        }

        session.setLearnerId(learnerId);
        session.setStatus(SessionStatus.REQUESTED);

        Session saved = repository.save(session);
        log.info("🔥 BEFORE PUBLISH EVENT for session request");
        publishEvent(saved);

        return saved;
    }

    /* ================================================================
     * METHOD: acceptSession
     * DESCRIPTION:
     * Mentors approve a requested slot, changing status to ACCEPTED
     * and firing an acceptance event.
     * ================================================================ */
    // ⭐ MENTOR ACCEPTS
    public Session acceptSession(Long id, Long userId) {

        Session session = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        Long mentorProfileId = mentorClient.getMentorProfileId(userId);

        if (!session.getMentorId().equals(mentorProfileId)) {
            throw new RuntimeException("You can accept only your sessions");
        }

        if (session.getStatus() != SessionStatus.REQUESTED) {
            throw new RuntimeException("Only requested sessions can be accepted");
        }

        session.setStatus(SessionStatus.ACCEPTED);

        Session saved = repository.save(session);
        log.info("🔥 BEFORE PUBLISH EVENT for session acceptance");
        publishEvent(saved);

        return saved;
    }

    /* ================================================================
     * METHOD: rejectSession
     * DESCRIPTION:
     * Mentors reject a requested slot if unavailable, updating status
     * to REJECTED.
     * ================================================================ */
    // ⭐ MENTOR REJECTS
    public Session rejectSession(Long id, Long userId) {

        Session session = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        Long mentorProfileId = mentorClient.getMentorProfileId(userId);

        if (!session.getMentorId().equals(mentorProfileId)) {
            throw new RuntimeException("You can reject only your sessions");
        }

        if (session.getStatus() != SessionStatus.REQUESTED) {
            throw new RuntimeException("Only requested sessions can be rejected");
        }

        session.setStatus(SessionStatus.REJECTED);
        return repository.save(session);
    }

    /* ================================================================
     * METHOD: cancelSession
     * DESCRIPTION:
     * Learners cancel their requested or accepted sessions before
     * they are completed.
     * ================================================================ */
    // ⭐ LEARNER CANCELS
    public Session cancelSession(Long id, Long learnerId) {

        Session session = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        if (session.getLearnerId() == null ||
            !session.getLearnerId().equals(learnerId)) {
            throw new RuntimeException("You can cancel only your booked session");
        }

        if (session.getStatus() == SessionStatus.COMPLETED) {
            throw new RuntimeException("Completed session cannot be cancelled");
        }

        session.setStatus(SessionStatus.CANCELLED);
        return repository.save(session);
    }

    /* ================================================================
     * METHOD: completeSession
     * DESCRIPTION:
     * Mentors mark an accepted session as COMPLETED after it finishes,
     * firing a final completion event.
     * ================================================================ */
    // ⭐ MENTOR COMPLETES
    public Session completeSession(Long id, Long userId) {

        Session session = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        Long mentorProfileId = mentorClient.getMentorProfileId(userId);

        if (!session.getMentorId().equals(mentorProfileId)) {
            throw new RuntimeException("You can complete only your sessions");
        }

        if (session.getStatus() != SessionStatus.ACCEPTED) {
            throw new RuntimeException("Only accepted sessions can be completed");
        }

        session.setStatus(SessionStatus.COMPLETED);

        Session saved = repository.save(session);
        log.info("🔥 BEFORE PUBLISH EVENT for session completion");
        publishEvent(saved);

        return saved;
    }

    /* ================================================================
     * METHOD: publishEvent
     * DESCRIPTION:
     * Private helper to construct and push SessionEvents onto the 
     * RabbitMQ 'session.queue'.
     * ================================================================ */
    private void publishEvent(Session session) {
    	log.info("🔥 publishEvent CALLED for session id: {}", session.getId());
        SessionEvent event = SessionEvent.builder()
                .sessionId(session.getId())
                .mentorId(session.getMentorId())
                .learnerId(session.getLearnerId())
                .status(session.getStatus().name())
                .sessionTime(session.getSessionTime())
                .build();

        rabbitTemplate.convertAndSend("session.queue", event);
        log.info("📤 EVENT SENT TO RABBITMQ: {}", event);
    }

    /* ================================================================
     * METHOD: getLearnerSessions
     * DESCRIPTION: Retrieves a list of sessions for a given learner.
     * ================================================================ */
    public List<Session> getLearnerSessions(Long learnerId) {
        return repository.findByLearnerId(learnerId);
    }

    /* ================================================================
     * METHOD: getMentorSessions
     * DESCRIPTION: Retrieves a raw list of sessions for a given mentor.
     * ================================================================ */
    public List<Session> getMentorSessions(Long mentorId) {
        return repository.findByMentorId(mentorId);
    }

    /* ================================================================
     * METHOD: getMentorSessionsPaged
     * DESCRIPTION: Retrieves paginated sessions for a mentor profile.
     * ================================================================ */
    public Page<Session> getMentorSessionsPaged(Long mentorId, Pageable pageable) {
        return repository.findByMentorId(mentorId, pageable);
    }

    /* ================================================================
     * METHOD: getLearnerSessionsPaged
     * DESCRIPTION: Retrieves paginated sessions booked by a learner.
     * ================================================================ */
    public Page<Session> getLearnerSessionsPaged(Long learnerId, Pageable pageable) {
        return repository.findByLearnerId(learnerId, pageable);
    }

    /* ================================================================
     * METHOD: getSessionsByStatus
     * DESCRIPTION: Fetches paginated sessions filtered by status.
     * ================================================================ */
    public Page<Session> getSessionsByStatus(SessionStatus status, Pageable pageable) {
        return repository.findByStatus(status, pageable);
    }

    /* ================================================================
     * METHOD: getSessionsByDateRange
     * DESCRIPTION: Fetches paginated sessions matching a time boundary.
     * ================================================================ */
    public Page<Session> getSessionsByDateRange(
            LocalDateTime start,
            LocalDateTime end,
            Pageable pageable) {

        return repository.findBySessionTimeBetween(start, end, pageable);
    }

    /* ================================================================
     * METHOD: isSessionCompleted
     * DESCRIPTION: Validates if a session's current status is COMPLETED.
     * ================================================================ */
    public Boolean isSessionCompleted(Long sessionId) {

        Session session = repository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        return session.getStatus() == SessionStatus.COMPLETED;
    }

    /* ================================================================
     * METHOD: getSession
     * DESCRIPTION: Retrieve a session summary mapped to SessionResponse.
     * ================================================================ */
    public SessionResponse getSession(Long id) {

        Session session = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        return SessionResponse.builder()
                .id(session.getId())
                .mentorId(session.getMentorId())
                .learnerId(session.getLearnerId())
                .status(session.getStatus().name())
                .build();
    }
}
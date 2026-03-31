package com.skillsync.session.repository;

import com.skillsync.session.entity.Session;
import com.skillsync.session.entity.SessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.List;

/*
 * ================================================================
 * AUTHOR: Ravindranath Potturu
 * CLASS: SessionRepository
 * DESCRIPTION:
 * Repository interface for managing mentoring sessions, with 
 * support for mentor/learner lookups and availability checks.
 * ================================================================
 */
public interface SessionRepository extends JpaRepository<Session, Long> {

    /* ================================================================
     * METHOD: findByMentorId
     * DESCRIPTION: Retrieves all sessions associated with a specific mentor.
     * ================================================================ */
    List<Session> findByMentorId(Long mentorId);

    /* ================================================================
     * METHOD: findByLearnerId
     * DESCRIPTION: Retrieves all sessions associated with a specific learner.
     * ================================================================ */
    List<Session> findByLearnerId(Long learnerId);

    List<Session> findByMentorIdAndSessionTimeBetween(
            Long mentorId,
            LocalDateTime start,
            LocalDateTime end
    );

    List<Session> findByStatus(SessionStatus status);
    
    Page<Session> findByMentorId(Long mentorId, Pageable pageable);

    Page<Session> findByLearnerId(Long learnerId, Pageable pageable);

    Page<Session> findByStatus(SessionStatus status, Pageable pageable);

    Page<Session> findBySessionTimeBetween(
            LocalDateTime start,
            LocalDateTime end,
            Pageable pageable);
}
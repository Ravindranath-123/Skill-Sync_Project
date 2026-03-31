package com.skillsync.session.controller;

import com.skillsync.session.dto.SessionRequestDTO;
import com.skillsync.session.dto.SessionResponse;
import com.skillsync.session.entity.Session;
import com.skillsync.session.entity.SessionStatus;
import com.skillsync.session.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;

import java.time.LocalDateTime;
import java.util.List;

/*
 * ================================================================
 * AUTHOR: Ravindranath Potturu
 * CLASS: SessionController
 * DESCRIPTION:
 * REST Controller mapping HTTP requests to Session lifecycle 
 * operations and fetching queries via the SessionService.
 * ================================================================
 */
@RestController
@RequestMapping("/sessions")
@RequiredArgsConstructor
public class SessionController {

    private final SessionService service;

    /* ================================================================
     * METHOD: createSlot
     * DESCRIPTION:
     * Exposes an endpoint allowing authenticated mentors to open 
     * new training slot timings.
     * ================================================================ */
    // ⭐ mentor creates slot
    @PostMapping("/createSlot")
    public Session createSlot(
            @RequestBody SessionRequestDTO dto,
            @AuthenticationPrincipal Jwt jwt) {

        Long mentorId = ((Number) jwt.getClaim("userId")).longValue();
        return service.createSlot(dto, mentorId);
    }

    /* ================================================================
     * METHOD: requestSlot
     * DESCRIPTION:
     * Endpoint for learners to claim an available mentor session slot.
     * ================================================================ */
    // ⭐ learner requests slot
    @PostMapping("/{id}/request")
    public Session requestSlot(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt) {

        Long learnerId = ((Number) jwt.getClaim("userId")).longValue();
        return service.requestSlot(id, learnerId);
    }

    /* ================================================================
     * METHOD: accept
     * DESCRIPTION:
     * Allows mentors to accept a learner's pending slot request.
     * ================================================================ */
    // ⭐ mentor accepts
    @PostMapping("/{id}/accept")
    public Session accept(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt) {

        Long mentorId = ((Number) jwt.getClaim("userId")).longValue();
        return service.acceptSession(id, mentorId);
    }

    /* ================================================================
     * METHOD: reject
     * DESCRIPTION:
     * Allows mentors to reject a learner's slot request.
     * ================================================================ */
    // ⭐ mentor rejects
    @PostMapping("/{id}/reject")
    public Session reject(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt) {

        Long mentorId = ((Number) jwt.getClaim("userId")).longValue();
        return service.rejectSession(id, mentorId);
    }

    /* ================================================================
     * METHOD: cancel
     * DESCRIPTION:
     * Lets learners cancel a session they previously requested or booked.
     * ================================================================ */
    // ⭐ learner cancels
    @PostMapping("/{id}/cancel")
    public Session cancel(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt) {

        Long learnerId = ((Number) jwt.getClaim("userId")).longValue();
        return service.cancelSession(id, learnerId);
    }

    /* ================================================================
     * METHOD: complete
     * DESCRIPTION:
     * Provides an endpoint for mentors to mark an accepted session 
     * as complete.
     * ================================================================ */
    // ⭐ mentor completes
    @PostMapping("/{id}/complete")
    public Session complete(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt) {

        Long mentorId = ((Number) jwt.getClaim("userId")).longValue();
        return service.completeSession(id, mentorId);
    }

    // ⭐ queries
    /* ================================================================
     * METHOD: mentorSessions
     * DESCRIPTION: Pull all sessions related to a mentor ID.
     * ================================================================ */
    @GetMapping("/mentor/{mentorId}")
    public List<Session> mentorSessions(@PathVariable Long mentorId) {
        return service.getMentorSessions(mentorId);
    }

    /* ================================================================
     * METHOD: learnerSessions
     * DESCRIPTION: Pull all sessions related to a learner ID.
     * ================================================================ */
    @GetMapping("/learner/{learnerId}")
    public List<Session> learnerSessions(@PathVariable Long learnerId) {
        return service.getLearnerSessions(learnerId);
    }

    /* ================================================================
     * METHOD: mentorPaged
     * DESCRIPTION: Pull paginated sessions for a given mentor ID.
     * ================================================================ */
    @GetMapping("/mentor/{mentorId}/paged")
    public Page<Session> mentorPaged(
            @PathVariable Long mentorId,
            @PageableDefault(size = 5) Pageable pageable) {

        return service.getMentorSessionsPaged(mentorId, pageable);
    }

    /* ================================================================
     * METHOD: learnerPaged
     * DESCRIPTION: Pull paginated sessions for a given learner ID.
     * ================================================================ */
    @GetMapping("/learner/{learnerId}/paged")
    public Page<Session> learnerPaged(
            @PathVariable Long learnerId,
            @PageableDefault(size = 5) Pageable pageable) {

        return service.getLearnerSessionsPaged(learnerId, pageable);
    }

    /* ================================================================
     * METHOD: statusFilter
     * DESCRIPTION: Filters sessions by matching their lifecycle status.
     * ================================================================ */
    @GetMapping("/status/{status}")
    public Page<Session> statusFilter(
            @PathVariable SessionStatus status,
            Pageable pageable) {

        return service.getSessionsByStatus(status, pageable);
    }

    /* ================================================================
     * METHOD: dateFilter
     * DESCRIPTION: Filters sessions falling inside a start/end date range.
     * ================================================================ */
    @GetMapping("/date-range")
    public Page<Session> dateFilter(
            @RequestParam LocalDateTime start,
            @RequestParam LocalDateTime end,
            Pageable pageable) {

        return service.getSessionsByDateRange(start, end, pageable);
    }

    /* ================================================================
     * METHOD: completed
     * DESCRIPTION: Returns boolean flag to check if a session is COMPLETED.
     * ================================================================ */
    @GetMapping("/{id}/completed")
    public Boolean completed(@PathVariable Long id) {
        return service.isSessionCompleted(id);
    }

    /* ================================================================
     * METHOD: getSession
     * DESCRIPTION: Fetches the overall response representation of a session.
     * ================================================================ */
    @GetMapping("/{id}")
    public SessionResponse getSession(@PathVariable Long id) {
        return service.getSession(id);
    }
}
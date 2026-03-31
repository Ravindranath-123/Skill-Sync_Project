package com.skillsync.review.repository;

import com.skillsync.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/*
 * ================================================================
 * AUTHOR: Ravindranath Potturu
 * CLASS: ReviewRepository
 * DESCRIPTION:
 * Repository interface for managing Review entities, supporting 
 * queries by mentor, learner, and session.
 * ================================================================
 */
public interface ReviewRepository extends JpaRepository<Review, Long> {

    /* ================================================================
     * METHOD: findByMentorId
     * DESCRIPTION: Retrieves all reviews for a specific mentor.
     * ================================================================ */
    List<Review> findByMentorId(Long mentorId);

    List<Review> findByLearnerId(Long learnerId);
    
    boolean existsBySessionId(Long sessionId);
    
}
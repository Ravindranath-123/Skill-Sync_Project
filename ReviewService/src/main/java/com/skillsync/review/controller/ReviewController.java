package com.skillsync.review.controller;

import com.skillsync.review.dto.ReviewRequest;
import com.skillsync.review.entity.Review;
import com.skillsync.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import jakarta.validation.Valid;

/*
 * ================================================================
 * AUTHOR: Ravindranath Potturu
 * CLASS: ReviewController
 * DESCRIPTION:
 * REST API Controller that exposes endpoints for submitting, editing,
 * deleting, and retrieving reviews and ratings.
 * ================================================================
 */
@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService service;

    /* ================================================================
     * METHOD: submit
     * DESCRIPTION:
     * HTTP POST endpoint for learners to submit a review for a 
     * recently completed session.
     * ================================================================ */
    // ⭐ SUBMIT REVIEW (SECURED)
    @PostMapping
    public Review submit(
            @Valid @RequestBody ReviewRequest request,
            @AuthenticationPrincipal Jwt jwt) {

        Long learnerId = ((Number) jwt.getClaim("userId")).longValue();

        return service.submitReview(request, learnerId);
    }

    /* ================================================================
     * METHOD: mentorReviews
     * DESCRIPTION: HTTP GET endpoint to fetch reviews given to a mentor.
     * ================================================================ */
    @GetMapping("/mentor/{mentorId}")
    public List<Review> mentorReviews(@PathVariable Long mentorId) {
        return service.getMentorReviews(mentorId);
    }

    /* ================================================================
     * METHOD: averageRating
     * DESCRIPTION: HTTP GET endpoint retrieving a mentor's average score.
     * ================================================================ */
    @GetMapping("/mentor/{mentorId}/average")
    public Double averageRating(@PathVariable Long mentorId) {
        return service.getAverageRating(mentorId);
    }

    /* ================================================================
     * METHOD: learnerReviews
     * DESCRIPTION: HTTP GET endpoint to fetch reviews written by a learner.
     * ================================================================ */
    @GetMapping("/learner/{learnerId}")
    public List<Review> learnerReviews(@PathVariable Long learnerId) {
        return service.getLearnerReviews(learnerId);
    }

    /* ================================================================
     * METHOD: editReview
     * DESCRIPTION: HTTP PUT endpoint allowing a user to edit their review.
     * ================================================================ */
    @PutMapping("/{id}")
    public Review editReview(
            @PathVariable Long id,
            @Valid @RequestBody ReviewRequest request,
            @AuthenticationPrincipal Jwt jwt) {

        Long learnerId = ((Number) jwt.getClaim("userId")).longValue();
        return service.editReview(id, request, learnerId);
    }

    /* ================================================================
     * METHOD: deleteReview
     * DESCRIPTION: HTTP DELETE endpoint allowing a user to delete their review.
     * ================================================================ */
    @DeleteMapping("/{id}")
    public String deleteReview(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt) {
            
        Long learnerId = ((Number) jwt.getClaim("userId")).longValue();
        service.deleteReview(id, learnerId);
        return "Review deleted successfully";
    }
}
package com.skillsync.review.dto;

import lombok.Data;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/*
 * ================================================================
 * AUTHOR: Ravindranath Potturu
 * CLASS: ReviewRequest
 * DESCRIPTION:
 * Data Transfer Object for submitting a new review or rating.
 * ================================================================
 */
@Data
public class ReviewRequest {

    @NotNull(message = "Session ID is required")
    private Long sessionId;

    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    private Integer rating;

    @NotBlank(message = "Comment cannot be blank")
    private String comment;
}
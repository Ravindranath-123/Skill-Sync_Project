package com.skillsync.review.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * ================================================================
 * AUTHOR: Ravindranath Potturu
 * CLASS: SessionDTO
 * DESCRIPTION:
 * Data Transfer Object representing session information within 
 * the Review Service context.
 * ================================================================
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionDTO {

    private Long id;
    private Long mentorId;
    private Long learnerId;
    private String status;
}
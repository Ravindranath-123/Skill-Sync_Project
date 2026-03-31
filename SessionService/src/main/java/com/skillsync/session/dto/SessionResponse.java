package com.skillsync.session.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * ================================================================
 * AUTHOR: Ravindranath Potturu
 * CLASS: SessionResponse
 * DESCRIPTION:
 * Data Transfer Object for returning mentoring session details 
 * in API responses.
 * ================================================================
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionResponse {

    private Long id;
    private Long mentorId;
    private Long learnerId;
    private String status;
}
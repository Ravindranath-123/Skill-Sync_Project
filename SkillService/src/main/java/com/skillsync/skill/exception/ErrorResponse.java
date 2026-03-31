package com.skillsync.skill.exception;

import lombok.*;

import java.time.LocalDateTime;

/*
 * ================================================================
 * AUTHOR: Ravindranath Potturu
 * CLASS: ErrorResponse
 * DESCRIPTION:
 * Data DTO for returning structured error information in the Skill Service.
 * ================================================================
 */
@Getter
@AllArgsConstructor
public class ErrorResponse {

    private LocalDateTime timestamp;
    private int status;
    private String message;
}
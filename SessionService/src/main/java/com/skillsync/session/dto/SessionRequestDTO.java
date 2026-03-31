package com.skillsync.session.dto;

import lombok.*;

import java.time.LocalDateTime;

/*
 * ================================================================
 * AUTHOR: Ravindranath Potturu
 * CLASS: SessionRequestDTO
 * DESCRIPTION:
 * Data Transfer Object for creating or requesting a mentoring session slot.
 * ================================================================
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SessionRequestDTO {

    private LocalDateTime sessionTime;
    private Integer durationMinutes;
}
package com.skillsync.mentor.dto;

import lombok.*;

/*
 * ================================================================
 * AUTHOR: Ravindranath Potturu
 * CLASS: UpdateMentorProfileRequestDto
 * DESCRIPTION:
 * Data Transfer Object for updating mentor profile information.
 * ================================================================
 */
@Data
public class UpdateMentorProfileRequestDto {

    private String bio;
    private Integer experienceYears;
    private Double hourlyRate;
    private Boolean available;
}
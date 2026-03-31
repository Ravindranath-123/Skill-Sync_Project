package com.skillsync.mentor.dto;

import jakarta.validation.constraints.*;
import lombok.*;

/*
 * ================================================================
 * AUTHOR: Ravindranath Potturu
 * CLASS: CreateMentorProfileRequestDto
 * DESCRIPTION:
 * Data Transfer Object for requesting the creation of a mentor profile.
 * ================================================================
 */
@Data
public class CreateMentorProfileRequestDto {

    @NotBlank
    private String bio;

    @NotNull
    private Integer experienceYears;

    @NotNull
    private Double hourlyRate;

    @NotNull
    private Boolean available;
}
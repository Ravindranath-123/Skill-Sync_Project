package com.skillsync.skill.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

/*
 * ================================================================
 * AUTHOR: Ravindranath Potturu
 * CLASS: CreateSkillRequestDto
 * DESCRIPTION:
 * Data Transfer Object for creating a new skill record.
 * ================================================================
 */
@Data
public class CreateSkillRequestDto {

    @NotBlank
    private String skillName;

    private String category;
}
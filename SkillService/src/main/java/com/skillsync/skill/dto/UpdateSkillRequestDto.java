package com.skillsync.skill.dto;

import lombok.*;

/*
 * ================================================================
 * AUTHOR: Ravindranath Potturu
 * CLASS: UpdateSkillRequestDto
 * DESCRIPTION:
 * Data Transfer Object for updating an existing skill's properties.
 * ================================================================
 */
@Data
public class UpdateSkillRequestDto {

    private String skillName;
    private String category;
    private Boolean active;
}
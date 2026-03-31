package com.skillsync.skill.dto;

import lombok.*;

/*
 * ================================================================
 * AUTHOR: Ravindranath Potturu
 * CLASS: SkillResponseDto
 * DESCRIPTION:
 * Data Transfer Object for returning skill details in API responses.
 * ================================================================
 */
@Getter
@Setter
@Builder
public class SkillResponseDto {

    private Long skillId;
    private String skillName;
    private String category;
    private Boolean active;
    private String message;
}
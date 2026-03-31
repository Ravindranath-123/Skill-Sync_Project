package com.skillsync.mentor.dto;

import lombok.*;

/*
 * ================================================================
 * AUTHOR: Ravindranath Potturu
 * CLASS: SkillDto
 * DESCRIPTION:
 * Data Transfer Object representing a skill in the Mentor Service.
 * ================================================================
 */
@Data
public class SkillDto {

    private Long skillId;
    private String skillName;
    private String category;
    private Boolean active;
}
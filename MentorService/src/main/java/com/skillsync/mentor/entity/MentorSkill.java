package com.skillsync.mentor.entity;

import jakarta.persistence.*;
import lombok.*;

/*
 * ================================================================
 * AUTHOR: Ravindranath Potturu
 * CLASS: MentorSkill
 * DESCRIPTION:
 * JPA Entity representing the association between a mentor and a 
 * specific skill.
 * ================================================================
 */
@Entity
@Table(
        name = "mentor_skills",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"mentorId", "skillId"}
        )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MentorSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long mentorId;

    private Long skillId;
}
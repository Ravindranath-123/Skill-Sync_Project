package com.skillsync.mentor.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.skillsync.mentor.entity.MentorSkill;

/*
 * ================================================================
 * AUTHOR: Ravindranath Potturu
 * CLASS: MentorSkillRepository
 * DESCRIPTION:
 * Repository interface for managing the mapping between mentors 
 * and their respective skills.
 * ================================================================
 */
public interface MentorSkillRepository
        extends JpaRepository<MentorSkill, Long> {

    List<MentorSkill> findBySkillId(Long skillId);

    /* ================================================================
     * METHOD: findByMentorId
     * DESCRIPTION: Retrieves all skills associated with a specific mentor.
     * ================================================================ */
    List<MentorSkill> findByMentorId(Long mentorId);
}
package com.skillsync.skill.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.skillsync.skill.entity.Skill;

import java.util.Optional;

/*
 * ================================================================
 * AUTHOR: Ravindranath Potturu
 * CLASS: SkillRepository
 * DESCRIPTION:
 * Repository interface for managing Skill entities, including 
 * specialized search and filtering methods.
 * ================================================================
 */
public interface SkillRepository extends JpaRepository<Skill, Long> {

    /* ================================================================
     * METHOD: findBySkillName
     * DESCRIPTION: Retrieves a skill by its exact unique name.
     * ================================================================ */
    Optional<Skill> findBySkillName(String skillName);

    /* ================================================================
     * METHOD: findByActiveTrue
     * DESCRIPTION: Returns a paginated list of all active skills.
     * ================================================================ */
    Page<Skill> findByActiveTrue(Pageable pageable);

    Page<Skill> findBySkillNameContainingIgnoreCase(
            String name,
            Pageable pageable
    );
    
    Boolean existsBySkillIdAndActiveTrue(Long skillId);
}
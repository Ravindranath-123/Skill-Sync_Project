package com.skillsync.skill.service;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;

import com.skillsync.skill.dto.*;
import com.skillsync.skill.entity.Skill;
import com.skillsync.skill.repository.SkillRepository;

import lombok.extern.slf4j.Slf4j;

/*
 * ================================================================
 * AUTHOR: Ravindranath Potturu
 * CLASS: SkillService
 * DESCRIPTION:
 * Service class that manages operations related to skills including 
 * creation, updating, retrieval, caching, and soft deletion.
 * ================================================================
 */
@Service
@Slf4j
public class SkillService {

    @Autowired
    private SkillRepository skillRepository;

    /* ================================================================
     * METHOD: createSkill
     * DESCRIPTION:
     * Validates and creates a new skill if it does not already exist,
     * and clears the skills cache to ensure consistency.
     * ================================================================ */
    @CacheEvict(value = "skills", allEntries = true)
    public SkillResponseDto createSkill(CreateSkillRequestDto request) {
        log.info("Creating skill with name: {}", request.getSkillName());

        skillRepository.findBySkillName(request.getSkillName())
                .ifPresent(s -> {
                    log.error("Skill already exists: {}", request.getSkillName());
                    throw new RuntimeException("Skill already exists");
                });

        Skill skill = Skill.builder()
                .skillName(request.getSkillName())
                .category(request.getCategory())
                .active(true)
                .createdAt(LocalDateTime.now())
                .build();

        skillRepository.save(skill);

        return map(skill, "Skill created");
    }

    /* ================================================================
     * METHOD: updateSkill
     * DESCRIPTION:
     * Updates an existing skill's details such as name, category,
     * and active status, then clears the related cache.
     * ================================================================ */
    @CacheEvict(value = "skills", allEntries = true)
    public SkillResponseDto updateSkill(
            Long skillId,
            UpdateSkillRequestDto request) {
        log.info("Updating skill with id: {}", skillId);

        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> {
                    log.error("Skill not found for id: {}", skillId);
                    return new RuntimeException("Skill not found");
                });

        if (request.getSkillName() != null)
            skill.setSkillName(request.getSkillName());

        if (request.getCategory() != null)
            skill.setCategory(request.getCategory());

        if (request.getActive() != null)
            skill.setActive(request.getActive());

        skillRepository.save(skill);

        return map(skill, "Skill updated");
    }

    /* ================================================================
     * METHOD: deleteSkill
     * DESCRIPTION:
     * Performs a hard delete of the skill from the database based
     * on the provided skill ID and evicts it from the cache.
     * ================================================================ */
    @CacheEvict(value = "skills", allEntries = true)
    public String deleteSkill(Long skillId) {
        log.info("Deleting skill with id: {}", skillId);

        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> {
                    log.error("Skill not found for id: {}", skillId);
                    return new RuntimeException("Skill not found");
                });

        skillRepository.delete(skill);

        return "Skill deleted";
    }

    /* ================================================================
     * METHOD: getAllActiveSkills
     * DESCRIPTION:
     * Retrieves a paginated list of all active skills from the 
     * database with caching enabled.
     * ================================================================ */
    @Cacheable(value = "skills")
    public Page<Skill> getAllActiveSkills(int page, int size) {

        Pageable pageable = PageRequest.of(page, size);
        return skillRepository.findByActiveTrue(pageable);
    }

    /* ================================================================
     * METHOD: searchSkills
     * DESCRIPTION:
     * Searches for active skills that match a given keyword in their
     * name, returning paginated and cached results.
     * ================================================================ */
    @Cacheable(value = "skills")
    public Page<Skill> searchSkills(
            String keyword,
            int page,
            int size) {

        Pageable pageable = PageRequest.of(page, size);
        return skillRepository
                .findBySkillNameContainingIgnoreCase(keyword, pageable);
    }

    /* ================================================================
     * METHOD: map
     * DESCRIPTION:
     * Helper method to map a Skill entity to a SkillResponseDto
     * adding a predefined status message.
     * ================================================================ */
    private SkillResponseDto map(Skill skill, String msg) {

        return SkillResponseDto.builder()
                .skillId(skill.getSkillId())
                .skillName(skill.getSkillName())
                .category(skill.getCategory())
                .active(skill.getActive())
                .message(msg)
                .build();
    }
    
    /* ================================================================
     * METHOD: skillExists
     * DESCRIPTION:
     * Checks if a skill exists by its ID and ensures that it is 
     * currently set as active.
     * ================================================================ */
    @Cacheable(value = "skills")
    public Boolean skillExists(Long skillId) {
        return skillRepository.existsBySkillIdAndActiveTrue(skillId);
    }
}
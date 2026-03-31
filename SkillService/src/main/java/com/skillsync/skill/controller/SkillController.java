package com.skillsync.skill.controller;

import com.skillsync.skill.dto.CreateSkillRequestDto;
import com.skillsync.skill.dto.SkillResponseDto;
import com.skillsync.skill.dto.UpdateSkillRequestDto;
import com.skillsync.skill.entity.Skill;
import com.skillsync.skill.service.SkillService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/*
 * ================================================================
 * AUTHOR: Ravindranath Potturu
 * CLASS: SkillController
 * DESCRIPTION:
 * REST Controller for managing skills, providing endpoints for 
 * creation, updating, searching, and admin-only deletion.
 * ================================================================
 */
@RestController
@RequestMapping("/skills")
@RequiredArgsConstructor
public class SkillController {

    private final SkillService skillService;

    /* ================================================================
     * METHOD: createSkill
     * DESCRIPTION:
     * Endpoint for admins to create a new skill in the system.
     * ================================================================ */
    // ⭐ ADMIN ONLY — Create Skill
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public SkillResponseDto createSkill(
            @Valid @RequestBody CreateSkillRequestDto request) {

        return skillService.createSkill(request);
    }

    /* ================================================================
     * METHOD: updateSkill
     * DESCRIPTION:
     * Endpoint for admins to update an existing skill's details 
     * based on its ID.
     * ================================================================ */
    // ⭐ ADMIN ONLY — Update Skill
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{skillId}")
    public SkillResponseDto updateSkill(
            @PathVariable Long skillId,
            @RequestBody UpdateSkillRequestDto request) {

        return skillService.updateSkill(skillId, request);
    }

    /* ================================================================
     * METHOD: deleteSkill
     * DESCRIPTION:
     * Endpoint for admins to permanently delete a skill from the 
     * database based on its ID.
     * ================================================================ */
    // ⭐ ADMIN ONLY — Soft Delete Skill
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{skillId}")
    public String deleteSkill(@PathVariable Long skillId) {

        return skillService.deleteSkill(skillId);
    }

    /* ================================================================
     * METHOD: getAllActiveSkills
     * DESCRIPTION:
     * Public endpoint to fetch all active skills with pagination,
     * typically used for marketplace dropdowns.
     * ================================================================ */
    // ⭐ PUBLIC — Get Active Skills (Marketplace dropdown)
    @GetMapping
    public Page<Skill> getAllActiveSkills(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return skillService.getAllActiveSkills(page, size);
    }

    /* ================================================================
     * METHOD: searchSkills
     * DESCRIPTION:
     * Public endpoint to search for skills based on a keyword.
     * ================================================================ */
    // ⭐ PUBLIC — Search Skills
    @GetMapping("/search")
    public Page<Skill> searchSkills(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return skillService.searchSkills(keyword, page, size);
    }

    /* ================================================================
     * METHOD: skillExists
     * DESCRIPTION:
     * Feign client validation endpoint to verify the existence 
     * of an active skill by its ID.
     * ================================================================ */
    // ⭐ FEIGN VALIDATION — Skill Exists
    @GetMapping("/exists/{skillId}")
    public Boolean skillExists(@PathVariable Long skillId) {

        return skillService.skillExists(skillId);
    }
}
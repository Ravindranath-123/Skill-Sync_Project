package com.skillsync.mentor.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import com.skillsync.mentor.dto.SkillDto;

/*
 * ================================================================
 * AUTHOR: Ravindranath Potturu
 * CLASS: SkillClient
 * DESCRIPTION:
 * This interface acts as a Feign Client to communicate with the
 * Skill Service. It provides a method to fetch skill details
 * by skill ID through a REST API call.
 * It enables seamless inter-service communication between the
 * Mentor Service and Skill Service in the microservices architecture.
 * ================================================================
 */

@FeignClient(name = "skill-service")
public interface SkillClient {

    @GetMapping("/skills/{skillId}")
    SkillDto getSkillById(@PathVariable("skillId") Long skillId);
}
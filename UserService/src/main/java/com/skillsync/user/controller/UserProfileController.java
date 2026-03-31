//package com.skillsync.user.controller;
//
//import com.skillsync.user.entity.UserProfile;
//import com.skillsync.user.service.UserProfileService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.security.oauth2.jwt.Jwt;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/users")
//@RequiredArgsConstructor
//public class UserProfileController {
//
//    private final UserProfileService service;
//
//    @PostMapping("/profile")
//    public UserProfile create(
//            @RequestBody UserProfile profile,
//            @AuthenticationPrincipal Jwt jwt) {
//            
//        Long userId = ((Number) jwt.getClaim("userId")).longValue();
//        profile.setUserId(userId);
//        return service.createProfile(profile);
//    }
//
//    @GetMapping("/{id}")
//    public UserProfile get(@PathVariable Long id) {
//        return service.getProfile(id);
//    }
//
//    @GetMapping
//    public List<UserProfile> getAll() {
//        return service.getAllProfiles();
//    }
//
//    @PutMapping("/profile")
//    public UserProfile update(
//            @RequestBody UserProfile profile,
//            @AuthenticationPrincipal Jwt jwt) {
//            
//        Long userId = ((Number) jwt.getClaim("userId")).longValue();
//        return service.updateProfile(userId, profile);
//    }
//    
//    @GetMapping("/exists/{id}")
//    public Boolean userExists(@PathVariable Long id) {
//        return service.userExists(id);
//    }
//}

package com.skillsync.user.controller;

import com.skillsync.user.dto.*;
import com.skillsync.user.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;

/*
 * ================================================================
 * AUTHOR: Ravindranath Potturu
 * CLASS: UserProfileController
 * DESCRIPTION:
 * REST Controller that exposes API endpoints for user profile 
 * management including retrieving, creating, and updating profiles.
 * ================================================================
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService service;

    /* ================================================================
     * METHOD: create
     * DESCRIPTION:
     * Handles the HTTP POST request to create a new user profile 
     * based on the provided request body and JWT authentication.
     * ================================================================ */
    // ✅ CREATE PROFILE
    @PostMapping("/profile")
    public UserProfileResponse create(
            @RequestBody UserProfileRequest request,
            @AuthenticationPrincipal Jwt jwt) {

        Long userId = ((Number) jwt.getClaim("userId")).longValue();
        return service.createProfile(userId, request);
    }

    /* ================================================================
     * METHOD: getMyProfile
     * DESCRIPTION:
     * Handles the HTTP GET request to fetch the profile of the 
     * currently authenticated user.
     * ================================================================ */
    // ✅ GET OWN PROFILE ONLY
    @GetMapping("/me")
    public UserProfileResponse getMyProfile(
            @AuthenticationPrincipal Jwt jwt) {

        Long userId = ((Number) jwt.getClaim("userId")).longValue();
        return service.getProfile(userId);
    }

    /* ================================================================
     * METHOD: userExists
     * DESCRIPTION:
     * Verifies if a user profile exists within the system cache 
     * or repository based on their ID. Intended for internal use.
     * ================================================================ */
    // ❗ INTERNAL USE ONLY (microservice call)
    @GetMapping("/exists/{id}")
    public Boolean userExists(@PathVariable Long id) {
        return service.userExists(id);
    }

    /* ================================================================
     * METHOD: update
     * DESCRIPTION:
     * Handles the HTTP PUT request to update the profile details 
     * of the currently authenticated user.
     * ================================================================ */
    // ✅ UPDATE OWN PROFILE
    @PutMapping("/profile")
    public UserProfileResponse update(
            @RequestBody UserProfileRequest request,
            @AuthenticationPrincipal Jwt jwt) {

        Long userId = ((Number) jwt.getClaim("userId")).longValue();
        return service.updateProfile(userId, request);
    }
}
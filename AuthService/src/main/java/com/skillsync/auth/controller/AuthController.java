package com.skillsync.auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.skillsync.auth.dto.LoginResponseDto;
import com.skillsync.auth.dto.ForgotPasswordRequestDto;
import com.skillsync.auth.dto.LoginRequestDto;
import com.skillsync.auth.dto.RegisterRequestDto;
import com.skillsync.auth.dto.RegisterResponseDto;
import com.skillsync.auth.dto.ResetPasswordRequestDto;
import com.skillsync.auth.repository.UserRepository;
import com.skillsync.auth.service.AuthService;

import jakarta.validation.Valid;

/*
 * ================================================================
 * AUTHOR: Ravindranath Potturu
 * CLASS: AuthController
 * DESCRIPTION:
 * REST Controller for authentication endpoints and internal user 
 * verification routes.
 * ================================================================
 */
@RestController
@RequestMapping("/auth")
@Validated
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;   // ⭐ IMPORTANT ADD

    /* ================================================================
     * METHOD: register
     * DESCRIPTION: Public endpoint for user registration.
     * ================================================================ */
    @PostMapping("/register")
    public RegisterResponseDto register(
            @Valid @RequestBody RegisterRequestDto request) {

        return authService.register(request);
    }

    /* ================================================================
     * METHOD: login
     * DESCRIPTION: Public endpoint for user login.
     * ================================================================ */
    @PostMapping("/login")
    public LoginResponseDto login(
            @Valid @RequestBody LoginRequestDto request) {

        return authService.login(request);
    }

    /* ================================================================
     * METHOD: userExists
     * DESCRIPTION: Internal route to verify user existence by ID.
     * ================================================================ */
    @GetMapping("/internal/users/{userId}")
    public Boolean userExists(@PathVariable Long userId) {
        return userRepository.existsById(userId);
    }

    /* ================================================================
     * METHOD: getUserEmail
     * DESCRIPTION: Internal route to fetch user email by ID.
     * ================================================================ */
    @GetMapping("/internal/users/{userId}/email")
    public String getUserEmail(@PathVariable Long userId) {
        return userRepository.findById(userId)
                .map(com.skillsync.auth.entity.User::getEmail)
                .orElse("unknown@example.com");
    }

    /* ================================================================
     * METHOD: getUserName
     * DESCRIPTION: Internal route to fetch username by ID.
     * ================================================================ */
    @GetMapping("/internal/users/{userId}/name")
    public String getUserName(@PathVariable Long userId) {
        return userRepository.findById(userId)
                .map(com.skillsync.auth.entity.User::getUsername)
                .orElse("User");
    }

    
    /* ================================================================
     * METHOD: forgotPassword
     * DESCRIPTION: Endpoint to trigger password reset flow.
     * ================================================================ */
    @PostMapping("/forgot-password")
    public String forgotPassword(
            @RequestBody ForgotPasswordRequestDto request) {

        return authService.forgotPassword(request.getEmail());
    }

    /* ================================================================
     * METHOD: resetPassword
     * DESCRIPTION: Endpoint to complete password reset using OTP.
     * ================================================================ */
    @PostMapping("/reset-password")
    public String resetPassword(
            @RequestBody ResetPasswordRequestDto request) {

        return authService.resetPassword(request);
    }
}
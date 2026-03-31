package com.skillsync.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.skillsync.auth.entity.PasswordResetOtp;

/*
 * ================================================================
 * AUTHOR: Ravindranath Potturu
 * CLASS: PasswordResetOtpRepository
 * DESCRIPTION:
 * Repository interface for managing password reset OTP tokens, 
 * supporting lookups and cleanup by email.
 * ================================================================
 */
public interface PasswordResetOtpRepository
        extends JpaRepository<PasswordResetOtp, Long> {

    /* ================================================================
     * METHOD: findByEmail
     * DESCRIPTION: Retrieves the latest OTP record for a given user email.
     * ================================================================ */
    Optional<PasswordResetOtp> findByEmail(String email);

    void deleteByEmail(String email);
}
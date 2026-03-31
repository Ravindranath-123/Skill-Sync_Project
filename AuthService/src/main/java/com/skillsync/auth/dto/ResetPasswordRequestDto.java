package com.skillsync.auth.dto;

import lombok.*;

/*
 * ================================================================
 * AUTHOR: Ravindranath Potturu
 * CLASS: ResetPasswordRequestDto
 * DESCRIPTION:
 * Data Transfer Object for resetting a user's password using an OTP.
 * ================================================================
 */
@Data
public class ResetPasswordRequestDto {

    private String email;
    private String otp;
    private String newPassword;
}
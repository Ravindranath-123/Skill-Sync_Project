package com.skillsync.auth.dto;

import jakarta.validation.constraints.Email;
import lombok.*;

/*
 * ================================================================
 * AUTHOR: Ravindranath Potturu
 * CLASS: ForgotPasswordRequestDto
 * DESCRIPTION:
 * Data Transfer Object for initiating the forgot password flow.
 * ================================================================
 */
@Data
public class ForgotPasswordRequestDto {

    @Email
    private String email;
}
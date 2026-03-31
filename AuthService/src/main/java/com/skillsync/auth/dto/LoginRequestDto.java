package com.skillsync.auth.dto;

import jakarta.validation.constraints.*;
import lombok.*;

/*
 * ================================================================
 * AUTHOR: Ravindranath Potturu
 * CLASS: LoginRequestDto
 * DESCRIPTION:
 * Data Transfer Object containing credentials for user login.
 * ================================================================
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDto {

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;
}
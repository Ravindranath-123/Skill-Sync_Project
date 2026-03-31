package com.skillsync.auth.dto;

import lombok.*;

/*
 * ================================================================
 * AUTHOR: Ravindranath Potturu
 * CLASS: RegisterResponseDto
 * DESCRIPTION:
 * Data Transfer Object for returning registration confirmation details.
 * ================================================================
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterResponseDto {

    private Long userId;
    private String username;
    private String email;
    private String role;
    private String message;
}
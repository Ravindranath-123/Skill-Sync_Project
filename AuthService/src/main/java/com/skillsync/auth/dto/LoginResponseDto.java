package com.skillsync.auth.dto;

import lombok.*;

/*
 * ================================================================
 * AUTHOR: Ravindranath Potturu
 * CLASS: LoginResponseDto
 * DESCRIPTION:
 * Data Transfer Object for returning authentication tokens and user details.
 * ================================================================
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponseDto {

    private Long userId;
    private String username;
    private String email;
    private String role;
    private String token;   
    private String message;

}
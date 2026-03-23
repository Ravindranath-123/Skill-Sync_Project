package com.skillsync.auth.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponseDto {

    private Long userId;
    private String username;
    private String email;
    private String role;
    private String token;   
    private String message;

}
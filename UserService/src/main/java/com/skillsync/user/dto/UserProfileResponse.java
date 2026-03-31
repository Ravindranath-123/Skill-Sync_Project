package com.skillsync.user.dto;

import lombok.*;

/*
 * ================================================================
 * AUTHOR: Ravindranath Potturu
 * CLASS: UserProfileResponse
 * DESCRIPTION:
 * Data Transfer Object for returning user profile details.
 * ================================================================
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileResponse {
    private Long userId;
    private String fullName;
    private String headline;
    private String bio;
    private String phone;
    private String timezone;
}

package com.skillsync.user.entity;

import jakarta.persistence.*;
import lombok.*;

/*
 * ================================================================
 * AUTHOR: Ravindranath Potturu
 * CLASS: UserProfile
 * DESCRIPTION:
 * JPA Entity representing the extended profile details for a user.
 * ================================================================
 */
@Entity
@Table(name = "user_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfile {

    @Id
    private Long userId;   // same id from auth-service

    private String fullName;

    private String headline;

    private String bio;

    private String phone;

    private String timezone;
}
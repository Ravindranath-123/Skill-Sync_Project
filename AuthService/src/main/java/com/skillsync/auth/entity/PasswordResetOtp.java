package com.skillsync.auth.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/*
 * ================================================================
 * AUTHOR: Ravindranath Potturu
 * CLASS: PasswordResetOtp
 * DESCRIPTION:
 * JPA Entity for storing one-time password (OTP) tokens for password reset.
 * ================================================================
 */
@Entity
@Table(name = "password_reset_otp")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordResetOtp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String otp;

    private LocalDateTime expiryTime;
}
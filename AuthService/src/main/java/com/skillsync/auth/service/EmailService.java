package com.skillsync.auth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/*
 * ================================================================
 * AUTHOR: Ravindranath Potturu
 * CLASS: EmailService
 * DESCRIPTION:
 * Simple utility service for sending plain text emails, specifically 
 * used for dispatching password reset OTPs.
 * ================================================================
 */
@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    /* ================================================================
     * METHOD: sendOtp
     * DESCRIPTION: Sends a standard password reset OTP email to the user.
     * ================================================================ */
    public void sendOtp(String email, String otp) {

        SimpleMailMessage msg = new SimpleMailMessage();

        msg.setTo(email);
        msg.setSubject("SkillSync Password Reset OTP");
        msg.setText("Your OTP is: " + otp);

        mailSender.send(msg);
    }
}
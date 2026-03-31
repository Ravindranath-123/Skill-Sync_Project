package com.skillsync.notification.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/*
 * ================================================================
 * AUTHOR: Ravindranath Potturu
 * CLASS: EmailService
 * DESCRIPTION:
 * Service class responsible for sending formatted HTML emails 
 * asynchronously using the JavaMailSender config.
 * ================================================================
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    /* ================================================================
     * METHOD: sendEmail
     * DESCRIPTION:
     * Constructs and dispatches a multipart MIME email message to 
     * the specified recipient address.
     * ================================================================ */
    public void sendEmail(String to, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true); // true indicates HTML
            
            mailSender.send(message);
            log.info("Email successfully sent to: {}", to);
            
        } catch (MessagingException e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
        }
    }
}



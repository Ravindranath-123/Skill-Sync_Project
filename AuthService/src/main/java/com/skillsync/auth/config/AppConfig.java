package com.skillsync.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/*
 * ================================================================
 * AUTHOR: Ravindranath Potturu
 * CLASS: AppConfig
 * DESCRIPTION:
 * General application configuration for the Auth Service, including 
 * beans for password encoding.
 * ================================================================
 */
@Configuration
public class AppConfig {

    /* ================================================================
     * METHOD: passwordEncoder
     * DESCRIPTION: Returns the BCrypt password encoder bean.
     * ================================================================ */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
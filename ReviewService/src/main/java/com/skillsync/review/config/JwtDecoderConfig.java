package com.skillsync.review.config;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

/*
 * ================================================================
 * AUTHOR: Ravindranath Potturu
 * CLASS: JwtDecoderConfig
 * DESCRIPTION:
 * Configures the JWT decoder for the Review Service using the shared secret.
 * ================================================================
 */
@Configuration
public class JwtDecoderConfig {

    private final String SECRET = "skillsyncsecretkeyskillsyncsecretkey";

    /* ================================================================
     * METHOD: jwtDecoder
     * DESCRIPTION: Defines the bean for JWT decoding logic.
     * ================================================================ */
    @Bean
    public JwtDecoder jwtDecoder() {

        SecretKeySpec key =
                new SecretKeySpec(SECRET.getBytes(StandardCharsets.UTF_8), "HmacSHA256");

        return NimbusJwtDecoder.withSecretKey(key).build();
    }
}
package com.skillsync.skill.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.http.HttpMethod;

import java.util.List;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

/*
 * ================================================================
 * AUTHOR: Ravindranath Potturu
 * CLASS: SecurityConfig
 * DESCRIPTION:
 * Configures the security filter chain, RBAC rules, exception 
 * handling for unauthorized access, and CORS for Skill Service.
 * ================================================================
 */
@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

        private static final String SECRET = "skillsyncsecretkeyskillsyncsecretkey";

        // ⭐ inject custom handlers
        private final CustomAccessDeniedHandler customAccessDeniedHandler;
        private final CustomAuthEntryPoint customAuthEntryPoint;

        /* ================================================================
         * METHOD: filterChain
         * DESCRIPTION: Defines the access control and security 
         * configurations for the Skill Service.
         * ================================================================ */
        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

                http
                                .csrf(csrf -> csrf.disable())
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers(HttpMethod.GET, "/skills", "/skills/search", "/skills/exists/**").permitAll()
                                                .requestMatchers("/skills/**").hasRole("ADMIN")
                                                .anyRequest().permitAll())

                                // ⭐ ADD THIS BLOCK (very important)
                                .exceptionHandling(ex -> ex
                                                .accessDeniedHandler(customAccessDeniedHandler)
                                                .authenticationEntryPoint(customAuthEntryPoint))

                                .oauth2ResourceServer(oauth -> oauth.jwt(jwt -> jwt.jwtAuthenticationConverter(
                                                jwtAuthenticationConverter())));

                return http.build();
        }

        /* ================================================================
         * METHOD: jwtDecoder
         * DESCRIPTION: Configures the decoder for JWT authentication.
         * ================================================================ */
        @Bean
        public JwtDecoder jwtDecoder() {

                SecretKey key = new SecretKeySpec(SECRET.getBytes(), "HmacSHA256");

                return NimbusJwtDecoder
                                .withSecretKey(key)
                                .build();
        }

        /* ================================================================
         * METHOD: jwtAuthenticationConverter
         * DESCRIPTION: Converts JWT claims into granted authorities.
         * ================================================================ */
        @Bean
        public JwtAuthenticationConverter jwtAuthenticationConverter() {

                JwtGrantedAuthoritiesConverter converter = new JwtGrantedAuthoritiesConverter();

                converter.setAuthoritiesClaimName("role");
                converter.setAuthorityPrefix("");
                JwtAuthenticationConverter authConverter = new JwtAuthenticationConverter();

                authConverter.setJwtGrantedAuthoritiesConverter(converter);

                return authConverter;
        }
        

}
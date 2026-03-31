package com.skillsync.mentor.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

/*
 * ================================================================
 * AUTHOR: Ravindranath Potturu
 * CLASS: CorsConfig
 * DESCRIPTION:
 * CORS is centrally handled by the API Gateway. This configuration 
 * has been removed to prevent duplicate headers.
 * ================================================================
 */
@Configuration
public class CorsConfig {
    // CORS configuration removed - Gateway centrally manages CORS
}
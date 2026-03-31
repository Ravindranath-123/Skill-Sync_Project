package com.skillsync.skill.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.*;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

/*
 * ================================================================
 * AUTHOR: Ravindranath Potturu
 * CLASS: CustomAccessDeniedHandler
 * DESCRIPTION:
 * Handles access denial scenarios (403 Forbidden) when an 
 * authenticated user lacks the required RBAC permissions.
 * ================================================================
 */
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    /* ================================================================
     * METHOD: handle
     * DESCRIPTION: 
     * Invoked when a user lacks proper authority. Sends a 403 
     * Forbidden JSON response.
     * ================================================================ */
    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException)
            throws IOException {

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        Map<String, Object> body = Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "status", 403,
                "message", "Access denied. Admin role required",
                "path", request.getRequestURI()
        );

        new ObjectMapper().writeValue(response.getOutputStream(), body);
    }
}
package com.skillsync.session.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/*
 * ================================================================
 * AUTHOR: Ravindranath Potturu
 * CLASS: JwtAuthEntryPoint
 * DESCRIPTION:
 * Handles unauthorized JWT access by returning a 401 response 
 * with a custom JSON error message.
 * ================================================================
 */
@Component
public class JwtAuthEntryPoint implements AuthenticationEntryPoint {

    /* ================================================================
     * METHOD: commence
     * DESCRIPTION: Triggered on auth failure. Returns 401 JSON error.
     * ================================================================ */
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         org.springframework.security.core.AuthenticationException ex)
            throws IOException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");

        response.getWriter().write("""
            {
              "error": "UNAUTHORIZED",
              "message": "JWT token missing or invalid. Please login."
            }
        """);
    }
}
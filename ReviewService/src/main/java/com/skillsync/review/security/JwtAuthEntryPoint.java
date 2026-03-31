package com.skillsync.review.security;

import jakarta.servlet.ServletException;
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
 * Standard entry point for Review Service to capture and handle 
 * unauthenticated requests, returning a 401 JSON response.
 * ================================================================
 */
@Component
public class JwtAuthEntryPoint implements AuthenticationEntryPoint {

    /* ================================================================
     * METHOD: commence
     * DESCRIPTION: Triggered on auth failure. Returns 401 JSON.
     * ================================================================ */
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         org.springframework.security.core.AuthenticationException authException)
            throws IOException, ServletException {

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        response.getWriter().write("""
            {
              "error": "UNAUTHORIZED",
              "message": "JWT token is missing or invalid. Please login first."
            }
        """);
    }
}
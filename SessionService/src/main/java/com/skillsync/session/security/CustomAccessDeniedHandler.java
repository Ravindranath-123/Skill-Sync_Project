package com.skillsync.session.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/*
 * ================================================================
 * AUTHOR: Ravindranath Potturu
 * CLASS: CustomAccessDeniedHandler
 * DESCRIPTION:
 * Handles 403 Forbidden errors when an authenticated user lacks 
 * the necessary roles to access a session resource.
 * ================================================================
 */
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    /* ================================================================
     * METHOD: handle
     * DESCRIPTION: Triggered on access denial. Returns 403 JSON error.
     * ================================================================ */
    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException ex)
            throws IOException {

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");

        response.getWriter().write("""
            {
              "error": "FORBIDDEN",
              "message": "You do not have permission to perform this action."
            }
        """);
    }
}
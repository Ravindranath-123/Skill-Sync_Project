package com.skillsync.review.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/*
 * ================================================================
 * AUTHOR: Ravindranath Potturu
 * CLASS: CustomAccessDeniedHandler
 * DESCRIPTION:
 * Handles 403 Forbidden scenarios for the Review Service, returning 
 * a localized error message for unauthorized role access.
 * ================================================================
 */
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    /* ================================================================
     * METHOD: handle
     * DESCRIPTION: Triggered when access is denied. Returns 403 JSON.
     * ================================================================ */
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("timestamp", LocalDateTime.now().toString());
        errorDetails.put("error", "ACCESS_DENIED");
        errorDetails.put("message", "Only specific roles (e.g., Learners) can perform this action.");

        ObjectMapper mapper = new ObjectMapper();
        response.getWriter().write(mapper.writeValueAsString(errorDetails));
    }
}

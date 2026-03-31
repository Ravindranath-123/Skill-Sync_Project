package com.skillsync.session.exception;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Map;

/*
 * ================================================================
 * AUTHOR: Ravindranath Potturu
 * CLASS: GlobalExceptionHandler
 * DESCRIPTION:
 * Main exception handler for the Session Service, capturing runtime 
 * errors and transforming them into structured map-based responses.
 * ================================================================
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /* ================================================================
     * METHOD: handleRuntime
     * DESCRIPTION: Catch-all handler for unhandled runtime exceptions.
     * ================================================================ */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleRuntime(RuntimeException ex) {

        return Map.of(
                "timestamp", LocalDateTime.now(),
                "error", "BUSINESS_ERROR",
                "message", ex.getMessage()
        );
    }
}
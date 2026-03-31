package com.skillsync.review.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/*
 * ================================================================
 * AUTHOR: Ravindranath Potturu
 * CLASS: GlobalExceptionHandler
 * DESCRIPTION:
 * Global exception handler for the Review Service, transforming 
 * exceptions into consistent map-based error responses.
 * ================================================================
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /* ================================================================
     * METHOD: handleHttpMessageNotReadable
     * DESCRIPTION: Handles malformed request paylods.
     * ================================================================ */
    @ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleHttpMessageNotReadable(org.springframework.http.converter.HttpMessageNotReadableException ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("error", "INVALID_FORMAT");
        error.put("message", "Malformed JSON request. Please check your spelling and data types.");
        return error;
    }

    /* ================================================================
     * METHOD: handleValidationExceptions
     * DESCRIPTION: Consolidates and returns all validation constraint violations.
     * ================================================================ */
    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleValidationExceptions(org.springframework.web.bind.MethodArgumentNotValidException ex) {
        StringBuilder errors = new StringBuilder();
        ex.getBindingResult().getFieldErrors().forEach(e -> 
                errors.append(e.getField()).append(": ").append(e.getDefaultMessage()).append(" | ")
        );

        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("error", "VALIDATION_ERROR");
        error.put("message", errors.toString());
        return error;
    }

    /* ================================================================
     * METHOD: handleRuntime
     * DESCRIPTION: General catch-all for runtime errors in the Review Service.
     * ================================================================ */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleRuntime(RuntimeException ex) {

        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("error", "BUSINESS_ERROR");
        error.put("message", ex.getMessage());

        return error;
    }
}
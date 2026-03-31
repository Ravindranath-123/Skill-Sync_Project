package com.skillsync.mentor.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

/*
 * ================================================================
 * AUTHOR: Ravindranath Potturu
 * CLASS: GlobalExceptionHandler
 * DESCRIPTION:
 * Centralized exception handling component for the Mentor Service, 
 * providing structured error communication to clients.
 * ================================================================
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /* ================================================================
     * METHOD: handleHttpMessageNotReadable
     * DESCRIPTION: Handles malformed JSON and body-parsing issues.
     * ================================================================ */
    @ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(
            org.springframework.http.converter.HttpMessageNotReadableException ex,
            HttpServletRequest request) {

        ErrorResponse response = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Invalid Request Format")
                .message("Malformed JSON request. Please check your data types (e.g., boolean values must be exactly true or false).")
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /* ================================================================
     * METHOD: handleValidationExceptions
     * DESCRIPTION: Handles field-level validation errors from request bodies.
     * ================================================================ */
    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
            org.springframework.web.bind.MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        StringBuilder errors = new StringBuilder();
        ex.getBindingResult().getFieldErrors().forEach(error -> 
                errors.append(error.getField()).append(": ").append(error.getDefaultMessage()).append(" | ")
        );

        ErrorResponse response = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Validation Error")
                .message(errors.toString())
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /* ================================================================
     * METHOD: handleRuntime
     * DESCRIPTION: General handler for any unexpected runtime exceptions.
     * ================================================================ */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntime(
            RuntimeException ex,
            HttpServletRequest request) {

        ErrorResponse response = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Business Error")
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
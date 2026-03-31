package com.skillsync.skill.exception;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/*
 * ================================================================
 * AUTHOR: Ravindranath Potturu
 * CLASS: GlobalExceptionHandler
 * DESCRIPTION:
 * Main exception handler for the Skill Service, transforming 
 * runtime exceptions into a standardized ErrorResponse object.
 * ================================================================
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /* ================================================================
     * METHOD: handleRuntime
     * DESCRIPTION: Catch-all handler for unhandled runtime exceptions.
     * ================================================================ */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntime(RuntimeException ex) {

        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.CONFLICT.value(),
                ex.getMessage()
        );

        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

}
package com.example.coursemanager.config;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// Catches errors and returns friendly messages instead of ugly stack traces
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleError(Exception e) {
        return ResponseEntity.badRequest().body("Error: " + e.getMessage());
    }
}

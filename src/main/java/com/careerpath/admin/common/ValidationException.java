package com.careerpath.admin.common;

/**
 * Thrown when user input fails validation (missing fields, bad format, etc.).
 * Handled by GlobalExceptionHandler → returns HTTP 400.
 */
public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}

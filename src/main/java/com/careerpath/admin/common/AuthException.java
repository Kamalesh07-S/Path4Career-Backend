package com.careerpath.admin.common;

/**
 * Thrown when authentication/authorization fails (wrong credentials, expired token, etc.).
 * Handled by GlobalExceptionHandler → returns HTTP 401.
 */
public class AuthException extends RuntimeException {
    public AuthException(String message) {
        super(message);
    }
}

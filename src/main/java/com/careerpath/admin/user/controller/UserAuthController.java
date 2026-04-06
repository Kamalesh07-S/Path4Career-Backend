package com.careerpath.admin.user.controller;

import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.careerpath.admin.entity.User;
import com.careerpath.admin.repository.UserRepo;
import com.careerpath.admin.user.dto.LoginRequest;
import com.careerpath.admin.user.dto.ForgotPasswordRequest;
import com.careerpath.admin.user.dto.RegisterRequest;
import com.careerpath.admin.user.dto.ResetPasswordRequest;
import com.careerpath.admin.user.service.AuthService;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/v1/auth")
public class UserAuthController {

    private final AuthService authService;
    private final UserRepo userRepository;

    public UserAuthController(AuthService authService, UserRepo userRepository) {
        this.authService = authService;
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<java.util.Map<String, String>> register(@RequestBody RegisterRequest request) {
        authService.register(request);
        java.util.Map<String, String> response = new java.util.LinkedHashMap<>();
        response.put("message", "User registered successfully");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<java.util.Map<String, Object>> login(
            @RequestBody LoginRequest request,
            HttpServletResponse response) {

        String token = authService.login(request);

        // Use SameSite=Lax with Secure=false for local development.
        // SameSite=None REQUIRES Secure=true — browsers silently drop
        // cookies that have SameSite=None without Secure.
        ResponseCookie cookie = ResponseCookie.from("AUTH_TOKEN", token)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(Duration.ofMillis(authService.getJwtExpiration()))
                .sameSite("Lax")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        // Return token + user info in body so frontend can also use localStorage
        User user = userRepository.findByEmail(request.getEmail().trim().toLowerCase());
        java.util.Map<String, Object> body = new java.util.LinkedHashMap<>();
        body.put("token", token);
        body.put("username", user != null ? user.getUsername() : "User");
        body.put("role", user != null ? user.getRole() : "USER");

        return ResponseEntity.ok(body);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(Authentication authentication,
            HttpServletResponse response) {

        // Clear cookie with SameSite=Lax (must match the login cookie settings)
        ResponseCookie cookie = ResponseCookie.from("AUTH_TOKEN", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        if (authentication != null && authentication.getName() != null) {
            User user = userRepository.findByEmail(authentication.getName());
            if (user != null) {
                user.setLoggedIn(false);
                user.setLastLogoutAt(LocalDateTime.now());
                userRepository.save(user);
            }
        }

        return ResponseEntity.ok("Logout successful");
    }

    /**
     * Quick auth status check — returns 200 if logged in, 401 if not.
     */
    @GetMapping("/status")
    public ResponseEntity<java.util.Map<String, Object>> status(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }

        User user = userRepository.findByEmail(authentication.getName());
        java.util.Map<String, Object> result = new java.util.LinkedHashMap<>();
        result.put("loggedIn", true);
        result.put("email", authentication.getName());
        result.put("username", user != null ? user.getUsername() : "User");
        result.put("role", user != null ? user.getRole() : "STUDENT");
        return ResponseEntity.ok(result);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<java.util.Map<String, String>> forgotPassword(
            @RequestBody ForgotPasswordRequest request) {

        String message = authService.forgotPassword(request.getEmail());

        java.util.Map<String, String> response = new java.util.LinkedHashMap<>();
        response.put("message", message);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<java.util.Map<String, String>> resetPassword(
            @RequestBody ResetPasswordRequest request) {

        authService.resetPassword(request);

        java.util.Map<String, String> response = new java.util.LinkedHashMap<>();
        response.put("message", "Password reset successful");

        return ResponseEntity.ok(response);
    }
}

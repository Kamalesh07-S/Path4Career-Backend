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
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(
            @RequestBody LoginRequest request,
            HttpServletResponse response) {

        String token = authService.login(request);

        // Use ResponseCookie with SameSite=None for cross-origin support
        ResponseCookie cookie = ResponseCookie.from("AUTH_TOKEN", token)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(Duration.ofMillis(authService.getJwtExpiration()))
                .sameSite("None")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok(token);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(Authentication authentication,
            HttpServletResponse response) {

        // Clear cookie with SameSite=None
        ResponseCookie cookie = ResponseCookie.from("AUTH_TOKEN", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("None")
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

        String tempPassword = authService.forgotPassword(request.getEmail());

        java.util.Map<String, String> response = new java.util.LinkedHashMap<>();
        response.put("message", "Password has been reset successfully");
        response.put("temporaryPassword", tempPassword);

        return ResponseEntity.ok(response);
    }
}

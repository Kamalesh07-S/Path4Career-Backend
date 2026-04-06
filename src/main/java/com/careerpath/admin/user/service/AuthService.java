package com.careerpath.admin.user.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.careerpath.admin.common.AuthException;
import com.careerpath.admin.common.ValidationException;
import com.careerpath.admin.entity.User;
import com.careerpath.admin.repository.UserRepo;
import com.careerpath.admin.security.UserJwtUtil;
import com.careerpath.admin.user.dto.LoginRequest;
import com.careerpath.admin.user.dto.RegisterRequest;
import com.careerpath.admin.user.dto.ResetPasswordRequest;
import com.careerpath.admin.user.entity.ActivityType;

@Service
public class AuthService {

    private final UserRepo userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserJwtUtil userJwtUtil;
    private final ActivityService activityService;
    private final EmailService emailService;

    public AuthService(UserRepo userRepository,
            PasswordEncoder passwordEncoder,
            UserJwtUtil userJwtUtil,
            ActivityService activityService,
            EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userJwtUtil = userJwtUtil;
        this.activityService = activityService;
        this.emailService = emailService;
    }

    public void register(RegisterRequest request) {
        // ── Input validation ──
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            throw new ValidationException("Username is required");
        }
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new ValidationException("Email is required");
        }
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new ValidationException("Password is required");
        }
        if (request.getPassword().trim().length() < 6) {
            throw new ValidationException("Password must be at least 6 characters");
        }

        // ── Normalize inputs ──
        String email = request.getEmail().trim().toLowerCase();
        String username = request.getUsername().trim();

        User existing = userRepository.findByEmail(email);
        if (existing != null) {
            throw new ValidationException("Email already registered");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(request.getPassword().trim()));
        user.setRole(request.getRole() != null ? request.getRole().toUpperCase() : "USER");
        user.setStudentType(request.getStudentType());
        user.setEnabled(true);
        user.setLoggedIn(false);
        user.setCreatedAt(LocalDateTime.now());

        userRepository.save(user);

        activityService.logActivity(user, ActivityType.REGISTER,
                "Account created as " + user.getRole());
    }

    public String login(LoginRequest request) {
        // ── Input validation ──
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new ValidationException("Email is required");
        }
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new ValidationException("Password is required");
        }

        String email = request.getEmail().trim().toLowerCase();
        String password = request.getPassword().trim();

        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new AuthException("Invalid email or password");
        }

        if (!user.isEnabled()) {
            throw new AuthException("Account is disabled");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new AuthException("Invalid email or password");
        }

        user.setLoggedIn(true);
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        activityService.logActivity(user, ActivityType.LOGIN,
                "Logged in successfully");

        return userJwtUtil.generateToken(user.getEmail());
    }

    public long getJwtExpiration() {
        return userJwtUtil.getExpiration();
    }

    /**
     * Forgot password: look up user by email, generate a reset token,
     * save the hashed version to the DB, and send a reset link via email.
     */
    public String forgotPassword(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new ValidationException("Email is required");
        }

        User user = userRepository.findByEmail(email.trim().toLowerCase());

        // Even if user not found, we return a success-like message to prevent email enumeration
        if (user != null) {
            String resetToken = UUID.randomUUID().toString();
            user.setResetPasswordToken(resetToken);
            user.setResetPasswordTokenExpiry(LocalDateTime.now().plusHours(1));
            userRepository.save(user);

            // Send email asynchronously (if possible, but here it's synchronous)
            emailService.sendPasswordResetEmail(user.getEmail(), resetToken);

            activityService.logActivity(user, ActivityType.PASSWORD_CHANGE,
                    "Password reset link sent via email");
        }

        return "If your email is registered, you will receive a reset link shortly.";
    }

    public void resetPassword(ResetPasswordRequest request) {
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new ValidationException("Email is required");
        }

        User user = userRepository.findByEmail(request.getEmail().trim().toLowerCase());
        if (user == null) {
            throw new AuthException("No account found with this email");
        }

        if (request.getToken() == null || request.getToken().trim().isEmpty()) {
            throw new ValidationException("Reset token is required");
        }

        if (user.getResetPasswordToken() == null
                || !user.getResetPasswordToken().equals(request.getToken().trim())
                || user.getResetPasswordTokenExpiry() == null
                || user.getResetPasswordTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new AuthException("Reset token is invalid or has expired");
        }

        if (request.getNewPassword() == null || request.getNewPassword().trim().length() < 6) {
            throw new ValidationException("New password must be at least 6 characters");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword().trim()));
        user.setResetPasswordToken(null);
        user.setResetPasswordTokenExpiry(null);
        userRepository.save(user);

        activityService.logActivity(user, ActivityType.PASSWORD_CHANGE,
                "Password updated via reset password token");
    }

}

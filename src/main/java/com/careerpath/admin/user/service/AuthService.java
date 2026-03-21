package com.careerpath.admin.user.service;

import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.careerpath.admin.entity.User;
import com.careerpath.admin.repository.UserRepo;
import com.careerpath.admin.security.UserJwtUtil;
import com.careerpath.admin.user.dto.LoginRequest;
import com.careerpath.admin.user.dto.RegisterRequest;
import com.careerpath.admin.user.entity.ActivityType;

@Service
public class AuthService {

    private final UserRepo userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserJwtUtil userJwtUtil;
    private final ActivityService activityService;

    public AuthService(UserRepo userRepository,
            PasswordEncoder passwordEncoder,
            UserJwtUtil userJwtUtil,
            ActivityService activityService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userJwtUtil = userJwtUtil;
        this.activityService = activityService;
    }

    public void register(RegisterRequest request) {
        User existing = userRepository.findByEmail(request.getEmail());
        if (existing != null) {
            throw new RuntimeException("Email already registered");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
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
        String email = request.getEmail().trim().toLowerCase();
        String password = request.getPassword().trim();

        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("Email not found");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Password mismatch");
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
     * Forgot password: look up user by email, generate a temporary password,
     * save the hashed version to the DB, and return the plain temp password.
     * In production, this would be sent via email instead of returned directly.
     */
    public String forgotPassword(String email) {
        User user = userRepository.findByEmail(email.trim().toLowerCase());
        if (user == null) {
            throw new RuntimeException("No account found with this email");
        }

        // Generate a random 8-character temporary password
        String tempPassword = generateTempPassword();

        // Hash and save to DB
        user.setPassword(passwordEncoder.encode(tempPassword));
        userRepository.save(user);

        activityService.logActivity(user, ActivityType.PASSWORD_CHANGE,
                "Password reset via forgot password");

        return tempPassword;
    }

    private String generateTempPassword() {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz23456789";
        StringBuilder sb = new StringBuilder();
        java.util.Random random = new java.util.Random();
        for (int i = 0; i < 8; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
}

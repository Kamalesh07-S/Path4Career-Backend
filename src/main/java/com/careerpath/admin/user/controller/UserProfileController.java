package com.careerpath.admin.user.controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.careerpath.admin.entity.User;
import com.careerpath.admin.repository.UserRepo;
import com.careerpath.admin.user.dto.ChangePasswordRequest;
import com.careerpath.admin.user.dto.ProfileResponse;
import com.careerpath.admin.user.dto.UpdateProfileRequest;
import com.careerpath.admin.user.service.UserService;

@RestController
@RequestMapping("/api/v1/user")
public class UserProfileController {

    private final UserRepo userRepository;
    private final UserService userService;

    public UserProfileController(UserRepo userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @GetMapping("/profile")
    public ProfileResponse getProfile(Authentication authentication) {
        return userService.getProfile(authentication);
    }

    @PutMapping("/profile")
    public void updateProfile(Authentication authentication,
            @RequestBody UpdateProfileRequest request) {
        userService.updateProfile(authentication, request);
    }

    @PostMapping("/change-password")
    public void changePassword(Authentication authentication,
            @RequestBody ChangePasswordRequest request) {
        userService.changePassword(authentication, request);
    }

    @GetMapping("/account-info")
    public ResponseEntity<Map<String, Object>> getAccountInfo(Principal principal) {
        User user = userRepository.findByEmail(principal.getName());
        if (user == null)
            throw new RuntimeException("User not found");

        Map<String, Object> info = new LinkedHashMap<>();
        info.put("lastLoginAt", user.getLastLoginAt() != null
                ? user.getLastLoginAt().toString()
                : null);
        info.put("loggedIn", user.isLoggedIn());
        info.put("createdAt", user.getCreatedAt() != null
                ? user.getCreatedAt().toString()
                : null);
        info.put("role", user.getRole());

        return ResponseEntity.ok(info);
    }

    @PostMapping("/logout-all")
    public ResponseEntity<String> logoutAll(Principal principal) {
        User user = userRepository.findByEmail(principal.getName());
        if (user == null)
            throw new RuntimeException("User not found");

        user.setLoggedIn(false);
        user.setLastLogoutAt(LocalDateTime.now());
        userRepository.save(user);

        return ResponseEntity.ok("Logged out from all devices");
    }
}

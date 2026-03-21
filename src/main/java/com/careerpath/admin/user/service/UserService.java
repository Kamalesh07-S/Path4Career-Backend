package com.careerpath.admin.user.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.careerpath.admin.entity.User;
import com.careerpath.admin.repository.UserRepo;
import com.careerpath.admin.user.dto.ChangePasswordRequest;
import com.careerpath.admin.user.dto.ProfileResponse;
import com.careerpath.admin.user.dto.UpdateProfileRequest;
import com.careerpath.admin.user.entity.ActivityType;
import com.careerpath.admin.user.entity.UserProfile;
import com.careerpath.admin.user.repository.UserProfileRepository;

import jakarta.transaction.Transactional;

@Service
public class UserService {

    private final UserProfileRepository userProfileRepository;
    private final UserRepo userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ActivityService activityService;

    public UserService(UserRepo userRepository,
            UserProfileRepository userProfileRepository,
            PasswordEncoder passwordEncoder,
            ActivityService activityService) {
        this.userRepository = userRepository;
        this.userProfileRepository = userProfileRepository;
        this.passwordEncoder = passwordEncoder;
        this.activityService = activityService;
    }

    private User resolveUser(Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName());
        if (user == null)
            throw new RuntimeException("User not found");
        return user;
    }

    public ProfileResponse getProfile(Authentication authentication) {
        User user = resolveUser(authentication);
        UserProfile profile = userProfileRepository.findByUser(user).orElse(null);

        if (profile != null) {
            return new ProfileResponse(
                    user.getUsername(), user.getEmail(), user.getRole(),
                    profile.getFullName(), profile.getPhone(),
                    profile.getDob(), profile.getGender(), profile.getLocation());
        }

        return new ProfileResponse(user.getUsername(), user.getEmail(), user.getRole());
    }

    @Transactional
    public void updateProfile(Authentication authentication, UpdateProfileRequest request) {
        User user = resolveUser(authentication);

        UserProfile profile = userProfileRepository.findByUser(user)
                .orElseGet(() -> {
                    UserProfile p = new UserProfile();
                    p.setUser(user);
                    return p;
                });

        profile.setFullName(request.getFullName());
        profile.setPhone(request.getPhone());
        profile.setDob(request.getDob());
        profile.setGender(request.getGender());
        profile.setLocation(request.getLocation());
        userProfileRepository.save(profile);

        activityService.logActivity(user, ActivityType.PROFILE_UPDATE,
                "Updated personal details");
    }

    public void changePassword(Authentication authentication, ChangePasswordRequest request) {
        User user = resolveUser(authentication);

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("Current password incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        activityService.logActivity(user, ActivityType.PASSWORD_CHANGE,
                "Password changed successfully");
    }
}

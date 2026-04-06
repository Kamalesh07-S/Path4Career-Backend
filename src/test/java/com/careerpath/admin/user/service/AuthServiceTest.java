package com.careerpath.admin.user.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.careerpath.admin.entity.User;
import com.careerpath.admin.repository.UserRepo;
import com.careerpath.admin.user.dto.ResetPasswordRequest;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AuthServiceTest {

    @Autowired
    private UserRepo userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthService authService;

    @Test
    void forgotPassword_generatesResetTokenAndSavesUser() {
        // Create and save a test user
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword(passwordEncoder.encode("oldpassword"));
        user.setRole("USER");
        user.setEnabled(true);
        user.setLoggedIn(false);
        user.setCreatedAt(LocalDateTime.now());
        userRepository.save(user);

        String resetToken = authService.forgotPassword("test@example.com");

        User updatedUser = userRepository.findByEmail("test@example.com");
        assertThat(resetToken).isNotBlank();
        assertThat(updatedUser.getResetPasswordToken()).isEqualTo(resetToken);
        assertThat(updatedUser.getResetPasswordTokenExpiry()).isAfter(LocalDateTime.now().minusSeconds(1));
    }

    @Test
    void resetPassword_updatesPasswordAndClearsToken() {
        // Create and save a test user
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword(passwordEncoder.encode("oldpassword"));
        user.setRole("USER");
        user.setEnabled(true);
        user.setLoggedIn(false);
        user.setCreatedAt(LocalDateTime.now());
        user.setResetPasswordToken("valid-token");
        user.setResetPasswordTokenExpiry(LocalDateTime.now().plusMinutes(30));
        userRepository.save(user);

        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setEmail("test@example.com");
        request.setToken("valid-token");
        request.setNewPassword("new-password");

        authService.resetPassword(request);

        User updatedUser = userRepository.findByEmail("test@example.com");
        assertThat(passwordEncoder.matches("new-password", updatedUser.getPassword())).isTrue();
        assertThat(updatedUser.getResetPasswordToken()).isNull();
        assertThat(updatedUser.getResetPasswordTokenExpiry()).isNull();
    }
}

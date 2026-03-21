package com.careerpath.admin.user.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.careerpath.admin.entity.User;
import com.careerpath.admin.repository.UserRepo;
import com.careerpath.admin.user.entity.UserPreference;
import com.careerpath.admin.user.repository.UserPreferenceRepository;

@RestController
@RequestMapping("/api/v1/user/preferences")
public class UserPreferenceController {

    private final UserRepo userRepo;
    private final UserPreferenceRepository prefRepo;

    public UserPreferenceController(UserRepo userRepo,
            UserPreferenceRepository prefRepo) {
        this.userRepo = userRepo;
        this.prefRepo = prefRepo;
    }

    @GetMapping
    public UserPreference getPrefs(Authentication auth) {
        User user = userRepo.findByEmail(auth.getName());
        if (user == null)
            throw new RuntimeException("User not found");

        return prefRepo.findByUser(user)
                .orElseGet(() -> {
                    UserPreference p = new UserPreference();
                    p.setUser(user);
                    return prefRepo.save(p);
                });
    }

    @PutMapping
    public void updatePrefs(@RequestBody UserPreference req, Authentication auth) {
        User user = userRepo.findByEmail(auth.getName());
        if (user == null)
            throw new RuntimeException("User not found");

        UserPreference pref = prefRepo.findByUser(user).orElseThrow();
        pref.setEmailNotifications(req.isEmailNotifications());
        pref.setPlatformNotifications(req.isPlatformNotifications());
        pref.setJobAlerts(req.isJobAlerts());
        pref.setTheme(req.getTheme());
        pref.setLearningPath(req.getLearningPath());
        prefRepo.save(pref);
    }
}

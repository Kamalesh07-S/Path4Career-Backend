package com.careerpath.admin.user.controller;

import com.careerpath.admin.entity.User;
import com.careerpath.admin.repository.UserRepo;
import com.careerpath.admin.user.repository.UserModuleCompletionRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/modules")
public class ModuleCompletionController {

    private final UserModuleCompletionRepository moduleCompletionRepo;
    private final UserRepo userRepo;

    public ModuleCompletionController(UserModuleCompletionRepository moduleCompletionRepo, UserRepo userRepo) {
        this.moduleCompletionRepo = moduleCompletionRepo;
        this.userRepo = userRepo;
    }

    /**
     * Get all completed module IDs for the authenticated user.
     */
    @GetMapping("/completed")
    public ResponseEntity<List<Long>> getCompletedModules(Authentication authentication) {
        User user = userRepo.findByEmail(authentication.getName());
        if (user == null) {
            return ResponseEntity.badRequest().build();
        }

        List<Long> completedIds = moduleCompletionRepo.findByUser(user).stream()
                .map(c -> c.getModule().getId())
                .collect(Collectors.toList());

        return ResponseEntity.ok(completedIds);
    }
}

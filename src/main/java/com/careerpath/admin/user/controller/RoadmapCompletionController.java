package com.careerpath.admin.user.controller;

import com.careerpath.admin.entity.Module;
import com.careerpath.admin.entity.Roadmap;
import com.careerpath.admin.entity.RoadmapModule;
import com.careerpath.admin.entity.User;
import com.careerpath.admin.repository.RoadmapRepo;
import com.careerpath.admin.repository.UserRepo;
import com.careerpath.admin.user.repository.UserRoadmapCompletionRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.careerpath.admin.user.entity.UserRoadmapCompletion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import com.careerpath.admin.entity.Skill;
import com.careerpath.admin.user.repository.UserTutorialCompletionRepository;

@RestController
@RequestMapping("/api/v1/roadmaps")
public class RoadmapCompletionController {

    private final UserRoadmapCompletionRepository roadmapCompletionRepo;
    private final UserTutorialCompletionRepository tutorialCompletionRepo;
    private final RoadmapRepo roadmapRepo;
    private final UserRepo userRepo;

    public RoadmapCompletionController(UserRoadmapCompletionRepository roadmapCompletionRepo,
            UserTutorialCompletionRepository tutorialCompletionRepo, RoadmapRepo roadmapRepo,
            UserRepo userRepo) {
        this.roadmapCompletionRepo = roadmapCompletionRepo;
        this.tutorialCompletionRepo = tutorialCompletionRepo;
        this.roadmapRepo = roadmapRepo;
        this.userRepo = userRepo;
    }

    /**
     * Get all completed roadmap IDs for the authenticated user.
     */
    @GetMapping("/completed")
    public ResponseEntity<List<Long>> getCompletedRoadmaps(Authentication authentication) {
        User user = userRepo.findByEmail(authentication.getName());
        if (user == null) {
            return ResponseEntity.badRequest().build();
        }

        List<Long> completedIds = roadmapCompletionRepo.findByUser(user).stream()
                .map(c -> c.getRoadmap().getId())
                .collect(Collectors.toList());

        return ResponseEntity.ok(completedIds);
    }

    /**
     * Get roadmap completion progress for all roadmaps.
     */
    @GetMapping("/progress")
    public ResponseEntity<List<Map<String, Object>>> getRoadmapProgress(Authentication authentication) {
        User user = userRepo.findByEmail(authentication.getName());
        if (user == null) {
            return ResponseEntity.badRequest().build();
        }

        // Fetch all completed tutorial (skill) IDs for this user
        Set<Long> userCompletedSkillIds = tutorialCompletionRepo.findByUser(user)
                .stream()
                .map(c -> c.getSkill().getId())
                .collect(Collectors.toSet());

        List<Roadmap> roadmaps = roadmapRepo.findAll().stream()
                .filter(r -> r.isActive() && r.isPublished())
                .collect(Collectors.toList());

        List<Map<String, Object>> progressList = new ArrayList<>();

        for (Roadmap roadmap : roadmaps) {
            // Flatten all active skills from all active modules in the roadmap
            List<Skill> allActiveSkills = roadmap.getModules().stream()
                    .map(RoadmapModule::getModule)
                    .filter(Module::isActive)
                    .flatMap(m -> m.getSkills().stream())
                    .filter(Skill::isActive)
                    .collect(Collectors.toList());

            int totalSkills = allActiveSkills.size();
            long completedSkills = 0;

            if (totalSkills > 0) {
                completedSkills = allActiveSkills.stream()
                        .filter(s -> userCompletedSkillIds.contains(s.getId()))
                        .count();
            }

            int completionPercentage = totalSkills == 0 ? 0 : (int) ((completedSkills * 100) / totalSkills);

            Map<String, Object> progressData = new HashMap<>();
            progressData.put("roadmapId", roadmap.getId());
            progressData.put("completedSkills", completedSkills);
            progressData.put("totalSkills", totalSkills);
            progressData.put("completionPercentage", completionPercentage);

            progressList.add(progressData);
        }

        return ResponseEntity.ok(progressList);
    }

    /**
     * Explicitly mark a roadmap as completed from the frontend.
     */
    @PostMapping("/{roadmapId}/complete")
    public ResponseEntity<?> markRoadmapCompleted(@PathVariable Long roadmapId, Authentication authentication) {
        User user = userRepo.findByEmail(authentication.getName());
        if (user == null) {
            return ResponseEntity.badRequest().build();
        }

        if (roadmapCompletionRepo.existsByUserAndRoadmapId(user, roadmapId)) {
            return ResponseEntity.ok().build(); // Already completed
        }

        Roadmap roadmap = roadmapRepo.findById(roadmapId).orElse(null);
        if (roadmap == null || !roadmap.isActive() || !roadmap.isPublished()) {
            return ResponseEntity.badRequest().build();
        }

        UserRoadmapCompletion completion = new UserRoadmapCompletion();
        completion.setUser(user);
        completion.setRoadmap(roadmap);
        roadmapCompletionRepo.save(completion);

        return ResponseEntity.ok().build();
    }
}

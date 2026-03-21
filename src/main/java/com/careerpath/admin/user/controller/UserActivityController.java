package com.careerpath.admin.user.controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.careerpath.admin.entity.User;
import com.careerpath.admin.repository.UserRepo;
import com.careerpath.admin.user.entity.ActivityType;
import com.careerpath.admin.user.entity.UserSkill;
import com.careerpath.admin.user.repository.UserSkillRepository;
import com.careerpath.admin.user.service.ActivityService;

@RestController
@RequestMapping("/api/v1/activities")
public class UserActivityController {

    private final ActivityService activityService;
    private final UserRepo userRepository;
    private final UserSkillRepository userSkillRepository;

    public UserActivityController(ActivityService activityService,
            UserRepo userRepository,
            UserSkillRepository userSkillRepository) {
        this.activityService = activityService;
        this.userRepository = userRepository;
        this.userSkillRepository = userSkillRepository;
    }

    private User getUser(Principal principal) {
        User user = userRepository.findByEmail(principal.getName());
        if (user == null)
            throw new RuntimeException("User not found");
        return user;
    }

    @PostMapping("/course-complete")
    public ResponseEntity<String> courseCompleted(
            Principal principal,
            @RequestBody Map<String, String> body) {
        User user = getUser(principal);
        String courseName = body.getOrDefault("courseName", "a course");
        activityService.logActivity(user, ActivityType.COURSE_COMPLETE,
                "Completed course: " + courseName);
        return ResponseEntity.ok("Course completion tracked");
    }

    @PostMapping("/job-applied")
    public ResponseEntity<String> jobApplied(
            Principal principal,
            @RequestBody Map<String, String> body) {
        User user = getUser(principal);
        String jobTitle = body.getOrDefault("jobTitle", "a position");
        String company = body.getOrDefault("company", "");
        String description = "Applied for: " + jobTitle;
        if (!company.isEmpty())
            description += " at " + company;
        activityService.logActivity(user, ActivityType.JOB_APPLIED, description);
        return ResponseEntity.ok("Job application tracked");
    }

    @PostMapping("/skill-update")
    public ResponseEntity<String> skillUpdate(
            Principal principal,
            @RequestBody Map<String, Object> body) {
        User user = getUser(principal);
        String skillName = (String) body.getOrDefault("skillName", "Unknown");
        int progress = body.get("progress") instanceof Number
                ? ((Number) body.get("progress")).intValue()
                : 0;
        progress = Math.max(0, Math.min(100, progress));

        UserSkill skill = userSkillRepository.findByUserAndSkillName(user, skillName)
                .orElseGet(() -> {
                    UserSkill s = new UserSkill();
                    s.setUser(user);
                    s.setSkillName(skillName);
                    return s;
                });

        skill.setProgress(progress);
        skill.setUpdatedAt(LocalDateTime.now());
        userSkillRepository.save(skill);

        activityService.logActivity(user, ActivityType.SKILL_UPDATE,
                "Updated skill: " + skillName + " (" + progress + "%)");
        return ResponseEntity.ok("Skill updated");
    }

    @PostMapping("/learning")
    public ResponseEntity<String> learningActivity(
            Principal principal,
            @RequestBody Map<String, String> body) {
        User user = getUser(principal);
        String description = body.getOrDefault("description", "Learning activity");
        activityService.logActivity(user, ActivityType.LEARNING, description);
        return ResponseEntity.ok("Learning activity tracked");
    }
}

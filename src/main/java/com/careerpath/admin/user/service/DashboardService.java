package com.careerpath.admin.user.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.careerpath.admin.entity.User;
import com.careerpath.admin.repository.UserRepo;
import com.careerpath.admin.user.dto.DashboardSummaryResponse;
import com.careerpath.admin.user.entity.ActivityType;
import com.careerpath.admin.user.entity.UserActivity;
import com.careerpath.admin.user.entity.UserAchievement;
import com.careerpath.admin.user.entity.UserSkill;
import com.careerpath.admin.user.repository.UserActivityRepository;
import com.careerpath.admin.user.repository.UserSkillRepository;

@Service
public class DashboardService {

    private final UserRepo userRepository;
    private final UserSkillRepository userSkillRepository;
    private final UserActivityRepository userActivityRepository;
    private final AchievementService achievementService;

    public DashboardService(UserRepo userRepository,
            UserSkillRepository userSkillRepository,
            UserActivityRepository userActivityRepository,
            AchievementService achievementService) {
        this.userRepository = userRepository;
        this.userSkillRepository = userSkillRepository;
        this.userActivityRepository = userActivityRepository;
        this.achievementService = achievementService;
    }

    private User resolveUser(Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName());
        if (user == null)
            throw new RuntimeException("User not found");
        return user;
    }

    public DashboardSummaryResponse getDashboard(Authentication authentication) {
        User user = resolveUser(authentication);

        List<UserSkill> skills = userSkillRepository.findByUser(user);

        int overallSkillProgress = skills.isEmpty() ? 0
                : (int) skills.stream()
                        .mapToInt(UserSkill::getProgress)
                        .average()
                        .orElse(0);

        String skillLevel = overallSkillProgress < 40 ? "Beginner"
                : overallSkillProgress < 70 ? "Intermediate" : "Advanced";

        Map<String, Integer> skillProgress = skills.stream()
                .collect(Collectors.toMap(
                        UserSkill::getSkillName,
                        UserSkill::getProgress,
                        (a, b) -> b));

        List<UserActivity> activities = userActivityRepository
                .findByUserOrderByCreatedAtDesc(user);

        Set<LocalDate> activeDays = activities.stream()
                .map(a -> a.getCreatedAt().toLocalDate())
                .collect(Collectors.toSet());

        int learningStreak = 0;
        LocalDate day = LocalDate.now();
        while (activeDays.contains(day)) {
            learningStreak++;
            day = day.minusDays(1);
        }

        int coursesCompleted = (int) activities.stream()
                .filter(a -> a.getType() == ActivityType.COURSE_COMPLETE)
                .count();

        int jobsApplied = (int) activities.stream()
                .filter(a -> a.getType() == ActivityType.JOB_APPLIED)
                .count();

        achievementService.evaluateAndGrant(user, overallSkillProgress,
                coursesCompleted, jobsApplied);

        return new DashboardSummaryResponse(
                user.getUsername(), learningStreak, coursesCompleted,
                jobsApplied, skillLevel, overallSkillProgress, skillProgress);
    }

    public List<UserSkill> getSkills(Authentication authentication) {
        return userSkillRepository.findByUser(resolveUser(authentication));
    }

    public List<UserActivity> getActivities(Authentication authentication) {
        return userActivityRepository.findByUserOrderByCreatedAtDesc(
                resolveUser(authentication));
    }

    public List<UserAchievement> getAchievements(Authentication authentication) {
        return achievementService.getAchievementsForUser(resolveUser(authentication));
    }
}

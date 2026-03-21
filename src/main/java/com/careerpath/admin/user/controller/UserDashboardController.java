package com.careerpath.admin.user.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.careerpath.admin.user.dto.DashboardSummaryResponse;
import com.careerpath.admin.user.entity.UserActivity;
import com.careerpath.admin.user.entity.UserAchievement;
import com.careerpath.admin.user.entity.UserSkill;
import com.careerpath.admin.user.service.DashboardService;

@RestController
@RequestMapping("/api/dashboard")
public class UserDashboardController {

    private final DashboardService dashboardService;

    public UserDashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/summary")
    public DashboardSummaryResponse getDashboardSummary(Authentication authentication) {
        return dashboardService.getDashboard(authentication);
    }

    @GetMapping("/skills")
    public List<UserSkill> getUserSkills(Authentication authentication) {
        return dashboardService.getSkills(authentication);
    }

    @GetMapping("/activities")
    public List<UserActivity> getActivities(Authentication authentication) {
        return dashboardService.getActivities(authentication);
    }

    @GetMapping("/achievements")
    public List<UserAchievement> getAchievements(Authentication authentication) {
        return dashboardService.getAchievements(authentication);
    }
}

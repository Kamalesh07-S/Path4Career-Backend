package com.careerpath.admin.user.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<DashboardSummaryResponse> getDashboardSummary(Authentication authentication) {
        return ResponseEntity.ok(dashboardService.getDashboard(authentication));
    }

    @GetMapping("/skills")
    public ResponseEntity<List<UserSkill>> getUserSkills(Authentication authentication) {
        return ResponseEntity.ok(dashboardService.getSkills(authentication));
    }

    @GetMapping("/activities")
    public ResponseEntity<List<UserActivity>> getActivities(Authentication authentication) {
        return ResponseEntity.ok(dashboardService.getActivities(authentication));
    }

    @GetMapping("/achievements")
    public ResponseEntity<List<UserAchievement>> getAchievements(Authentication authentication) {
        return ResponseEntity.ok(dashboardService.getAchievements(authentication));
    }
}

package com.careerpath.admin.user.dto;

import java.util.Map;

public class DashboardSummaryResponse {
    private String username;
    private int learningStreak;
    private int coursesCompleted;
    private int jobsApplied;
    private String skillLevel;
    private int overallSkillProgress;
    private Map<String, Integer> skillProgress;

    public DashboardSummaryResponse(String username, int learningStreak,
            int coursesCompleted, int jobsApplied, String skillLevel,
            int overallSkillProgress, Map<String, Integer> skillProgress) {
        this.username = username;
        this.learningStreak = learningStreak;
        this.coursesCompleted = coursesCompleted;
        this.jobsApplied = jobsApplied;
        this.skillLevel = skillLevel;
        this.overallSkillProgress = overallSkillProgress;
        this.skillProgress = skillProgress;
    }

    public String getUsername() {
        return username;
    }

    public int getLearningStreak() {
        return learningStreak;
    }

    public int getCoursesCompleted() {
        return coursesCompleted;
    }

    public int getJobsApplied() {
        return jobsApplied;
    }

    public String getSkillLevel() {
        return skillLevel;
    }

    public int getOverallSkillProgress() {
        return overallSkillProgress;
    }

    public Map<String, Integer> getSkillProgress() {
        return skillProgress;
    }
}

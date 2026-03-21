package com.careerpath.admin.user.dto;

import java.time.LocalDateTime;

public class ModuleQuizResultDTO {

    private Long id;
    private Long moduleId;
    private String moduleName;
    private int score;
    private int totalQuestions;
    private int percentage;
    private boolean passed;
    private int timeTakenSeconds;
    private LocalDateTime attemptedAt;

    public ModuleQuizResultDTO(Long id, Long moduleId, String moduleName,
                                int score, int totalQuestions, int percentage,
                                boolean passed, int timeTakenSeconds,
                                LocalDateTime attemptedAt) {
        this.id = id;
        this.moduleId = moduleId;
        this.moduleName = moduleName;
        this.score = score;
        this.totalQuestions = totalQuestions;
        this.percentage = percentage;
        this.passed = passed;
        this.timeTakenSeconds = timeTakenSeconds;
        this.attemptedAt = attemptedAt;
    }

    // Getters
    public Long getId() { return id; }
    public Long getModuleId() { return moduleId; }
    public String getModuleName() { return moduleName; }
    public int getScore() { return score; }
    public int getTotalQuestions() { return totalQuestions; }
    public int getPercentage() { return percentage; }
    public boolean isPassed() { return passed; }
    public int getTimeTakenSeconds() { return timeTakenSeconds; }
    public LocalDateTime getAttemptedAt() { return attemptedAt; }
}

package com.careerpath.admin.user.dto;

import java.time.LocalDateTime;

public class UserQuizDTO {

    private Long id;
    private Long quizId;
    private String quizTitle;
    private String tutorialId;
    private int score;
    private int totalQuestions;
    private int percentage;
    private boolean passed;
    private int timeTakenSeconds;
    private LocalDateTime attemptedAt;

    public UserQuizDTO() {}

    public UserQuizDTO(Long id, Long quizId, String quizTitle, String tutorialId,
                       int score, int totalQuestions, int percentage, boolean passed,
                       int timeTakenSeconds, LocalDateTime attemptedAt) {
        this.id = id;
        this.quizId = quizId;
        this.quizTitle = quizTitle;
        this.tutorialId = tutorialId;
        this.score = score;
        this.totalQuestions = totalQuestions;
        this.percentage = percentage;
        this.passed = passed;
        this.timeTakenSeconds = timeTakenSeconds;
        this.attemptedAt = attemptedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getQuizId() { return quizId; }
    public void setQuizId(Long quizId) { this.quizId = quizId; }

    public String getQuizTitle() { return quizTitle; }
    public void setQuizTitle(String quizTitle) { this.quizTitle = quizTitle; }

    public String getTutorialId() { return tutorialId; }
    public void setTutorialId(String tutorialId) { this.tutorialId = tutorialId; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public int getTotalQuestions() { return totalQuestions; }
    public void setTotalQuestions(int totalQuestions) { this.totalQuestions = totalQuestions; }

    public int getPercentage() { return percentage; }
    public void setPercentage(int percentage) { this.percentage = percentage; }

    public boolean isPassed() { return passed; }
    public void setPassed(boolean passed) { this.passed = passed; }

    public int getTimeTakenSeconds() { return timeTakenSeconds; }
    public void setTimeTakenSeconds(int timeTakenSeconds) { this.timeTakenSeconds = timeTakenSeconds; }

    public LocalDateTime getAttemptedAt() { return attemptedAt; }
    public void setAttemptedAt(LocalDateTime attemptedAt) { this.attemptedAt = attemptedAt; }
}

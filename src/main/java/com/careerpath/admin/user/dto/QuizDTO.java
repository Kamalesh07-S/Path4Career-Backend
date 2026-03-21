package com.careerpath.admin.user.dto;

public class QuizDTO {

    private Long id;
    private String title;
    private String tutorialId;
    private String description;
    private int totalQuestions;
    private int timeLimitMinutes;
    private int passingPercentage;

    public QuizDTO() {}

    public QuizDTO(Long id, String title, String tutorialId, String description,
                   int totalQuestions, int timeLimitMinutes, int passingPercentage) {
        this.id = id;
        this.title = title;
        this.tutorialId = tutorialId;
        this.description = description;
        this.totalQuestions = totalQuestions;
        this.timeLimitMinutes = timeLimitMinutes;
        this.passingPercentage = passingPercentage;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getTutorialId() { return tutorialId; }
    public void setTutorialId(String tutorialId) { this.tutorialId = tutorialId; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getTotalQuestions() { return totalQuestions; }
    public void setTotalQuestions(int totalQuestions) { this.totalQuestions = totalQuestions; }

    public int getTimeLimitMinutes() { return timeLimitMinutes; }
    public void setTimeLimitMinutes(int timeLimitMinutes) { this.timeLimitMinutes = timeLimitMinutes; }

    public int getPassingPercentage() { return passingPercentage; }
    public void setPassingPercentage(int passingPercentage) { this.passingPercentage = passingPercentage; }
}

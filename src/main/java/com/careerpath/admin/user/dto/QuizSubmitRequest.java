package com.careerpath.admin.user.dto;

public class QuizSubmitRequest {

    private Long quizId;
    private String tutorialId;
    private int score;
    private int totalQuestions;
    private int percentage;
    private boolean passed;
    private int timeTakenSeconds;

    public Long getQuizId() { return quizId; }
    public void setQuizId(Long quizId) { this.quizId = quizId; }

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
}

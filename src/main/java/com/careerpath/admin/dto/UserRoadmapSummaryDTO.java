package com.careerpath.admin.dto;

public class UserRoadmapSummaryDTO {

    private Long id;
    private String title;
    private String jobRole;
    private String difficulty;
    private Integer durationValue;
    private String durationUnit;

    public UserRoadmapSummaryDTO() {
    }

    public UserRoadmapSummaryDTO(Long id, String title, String jobRole,
            String difficulty, Integer durationValue,
            String durationUnit) {
        this.id = id;
        this.title = title;
        this.jobRole = jobRole;
        this.difficulty = difficulty;
        this.durationValue = durationValue;
        this.durationUnit = durationUnit;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getJobRole() {
        return jobRole;
    }

    public void setJobRole(String jobRole) {
        this.jobRole = jobRole;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public Integer getDurationValue() {
        return durationValue;
    }

    public void setDurationValue(Integer durationValue) {
        this.durationValue = durationValue;
    }

    public String getDurationUnit() {
        return durationUnit;
    }

    public void setDurationUnit(String durationUnit) {
        this.durationUnit = durationUnit;
    }
}

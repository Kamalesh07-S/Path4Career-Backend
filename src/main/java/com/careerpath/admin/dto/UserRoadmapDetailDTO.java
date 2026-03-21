package com.careerpath.admin.dto;

import java.util.List;

public class UserRoadmapDetailDTO {

    private Long id;
    private String title;
    private String description;
    private String jobRole;
    private String difficulty;
    private Integer durationValue;
    private String durationUnit;
    private List<UserModuleSummaryDTO> modules;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public List<UserModuleSummaryDTO> getModules() {
        return modules;
    }

    public void setModules(List<UserModuleSummaryDTO> modules) {
        this.modules = modules;
    }
}

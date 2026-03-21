package com.careerpath.admin.dto;

import com.careerpath.admin.entity.Difficulty;
import com.careerpath.admin.entity.DurationUnit;

import java.time.LocalDateTime;
import java.util.List;

public class RoadmapDetailsDTO {

    private Long id;
    private String title;
    private String description;

    private boolean active;
    private boolean published;

    private LocalDateTime createdAt;
    private LocalDateTime lastModifiedAt;

    // aggregated values (can be null for now)
    private Integer durationValue;
    private DurationUnit durationUnit;
    private Difficulty difficulty;

    private List<RoadmapModuleDTO> modules;

    // getters & setters

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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getLastModifiedAt() {
        return lastModifiedAt;
    }

    public void setLastModifiedAt(LocalDateTime lastModifiedAt) {
        this.lastModifiedAt = lastModifiedAt;
    }

    public Integer getDurationValue() {
        return durationValue;
    }

    public void setDurationValue(Integer durationValue) {
        this.durationValue = durationValue;
    }

    public DurationUnit getDurationUnit() {
        return durationUnit;
    }

    public void setDurationUnit(DurationUnit durationUnit) {
        this.durationUnit = durationUnit;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public List<RoadmapModuleDTO> getModules() {
        return modules;
    }

    public void setModules(List<RoadmapModuleDTO> modules) {
        this.modules = modules;
    }
}

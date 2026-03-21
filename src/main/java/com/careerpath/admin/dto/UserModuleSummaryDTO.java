package com.careerpath.admin.dto;

import java.util.List;

public class UserModuleSummaryDTO {

    private Long moduleId;
    private String moduleName;
    private int orderIndex;
    private String difficulty;
    private Integer durationValue;
    private String durationUnit;
    private List<Long> skillIds;

    public UserModuleSummaryDTO() {
    }

    public UserModuleSummaryDTO(Long moduleId, String moduleName, int orderIndex,
            String difficulty, Integer durationValue, String durationUnit, List<Long> skillIds) {
        this.moduleId = moduleId;
        this.moduleName = moduleName;
        this.orderIndex = orderIndex;
        this.difficulty = difficulty;
        this.durationValue = durationValue;
        this.durationUnit = durationUnit;
        this.skillIds = skillIds;
    }

    public Long getModuleId() {
        return moduleId;
    }

    public void setModuleId(Long moduleId) {
        this.moduleId = moduleId;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public int getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(int orderIndex) {
        this.orderIndex = orderIndex;
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

    public List<Long> getSkillIds() {
        return skillIds;
    }

    public void setSkillIds(List<Long> skillIds) {
        this.skillIds = skillIds;
    }
}

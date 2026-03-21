package com.careerpath.admin.user.dto;

import java.time.LocalDateTime;

public class CertificateResponseDTO {

    private String certificateId;
    private Long moduleId;
    private String moduleName;
    private String moduleDescription;
    private String difficulty;
    private Integer durationValue;
    private String durationUnit;
    private int skillCount;
    private int scorePercentage;
    private String issuedToName;
    private LocalDateTime issuedAt;

    public CertificateResponseDTO() {}

    public CertificateResponseDTO(String certificateId, Long moduleId, String moduleName,
                                   String moduleDescription, String difficulty,
                                   Integer durationValue, String durationUnit,
                                   int skillCount, int scorePercentage,
                                   String issuedToName, LocalDateTime issuedAt) {
        this.certificateId = certificateId;
        this.moduleId = moduleId;
        this.moduleName = moduleName;
        this.moduleDescription = moduleDescription;
        this.difficulty = difficulty;
        this.durationValue = durationValue;
        this.durationUnit = durationUnit;
        this.skillCount = skillCount;
        this.scorePercentage = scorePercentage;
        this.issuedToName = issuedToName;
        this.issuedAt = issuedAt;
    }

    // Getters
    public String getCertificateId() { return certificateId; }
    public Long getModuleId() { return moduleId; }
    public String getModuleName() { return moduleName; }
    public String getModuleDescription() { return moduleDescription; }
    public String getDifficulty() { return difficulty; }
    public Integer getDurationValue() { return durationValue; }
    public String getDurationUnit() { return durationUnit; }
    public int getSkillCount() { return skillCount; }
    public int getScorePercentage() { return scorePercentage; }
    public String getIssuedToName() { return issuedToName; }
    public LocalDateTime getIssuedAt() { return issuedAt; }

    // Setters
    public void setCertificateId(String certificateId) { this.certificateId = certificateId; }
    public void setModuleId(Long moduleId) { this.moduleId = moduleId; }
    public void setModuleName(String moduleName) { this.moduleName = moduleName; }
    public void setModuleDescription(String moduleDescription) { this.moduleDescription = moduleDescription; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
    public void setDurationValue(Integer durationValue) { this.durationValue = durationValue; }
    public void setDurationUnit(String durationUnit) { this.durationUnit = durationUnit; }
    public void setSkillCount(int skillCount) { this.skillCount = skillCount; }
    public void setScorePercentage(int scorePercentage) { this.scorePercentage = scorePercentage; }
    public void setIssuedToName(String issuedToName) { this.issuedToName = issuedToName; }
    public void setIssuedAt(LocalDateTime issuedAt) { this.issuedAt = issuedAt; }
}

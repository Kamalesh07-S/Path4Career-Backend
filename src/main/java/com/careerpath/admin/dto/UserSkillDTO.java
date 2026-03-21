package com.careerpath.admin.dto;

public class UserSkillDTO {

    private Long id;
    private String name;
    private String category;
    private String level;
    private String description;
    private String tutorialId;

    public UserSkillDTO() {
    }

    public UserSkillDTO(Long id, String name, String category,
            String level, String description, String tutorialId) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.level = level;
        this.description = description;
        this.tutorialId = tutorialId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTutorialId() {
        return tutorialId;
    }

    public void setTutorialId(String tutorialId) {
        this.tutorialId = tutorialId;
    }
}

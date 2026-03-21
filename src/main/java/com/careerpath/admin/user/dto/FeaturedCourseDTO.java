package com.careerpath.admin.user.dto;

public class FeaturedCourseDTO {
    private String title;
    private String description;
    private String level;
    private String type;

    public FeaturedCourseDTO(String title, String description, String level, String type) {
        this.title = title;
        this.description = description;
        this.level = level;
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getLevel() {
        return level;
    }

    public String getType() {
        return type;
    }
}

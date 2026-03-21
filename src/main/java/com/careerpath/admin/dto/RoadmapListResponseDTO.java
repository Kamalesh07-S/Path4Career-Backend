package com.careerpath.admin.dto;

public class RoadmapListResponseDTO {

    private Long id;
    private String title;
    private String jobRoleName;
    private boolean published;
    private boolean active;

    public RoadmapListResponseDTO(Long id, String title,
                                  String jobRoleName,
                                  boolean published,
                                  boolean active) {
        this.id = id;
        this.title = title;
        this.jobRoleName = jobRoleName;
        this.published = published;
        this.active = active;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getJobRoleName() { return jobRoleName; }
    public boolean isPublished() { return published; }
    public boolean isActive() { return active; }
}

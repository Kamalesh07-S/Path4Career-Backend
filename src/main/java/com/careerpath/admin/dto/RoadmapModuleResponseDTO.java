package com.careerpath.admin.dto;

public class RoadmapModuleResponseDTO {

    private Long id;
    private Long moduleId;
    private String moduleName;
    private int orderIndex;

    public RoadmapModuleResponseDTO(Long id, Long moduleId, String moduleName, int orderIndex) {
        this.id = id;
        this.moduleId = moduleId;
        this.moduleName = moduleName;
        this.orderIndex = orderIndex;
    }

    public Long getId() { return id; }
    public Long getModuleId() { return moduleId; }
    public String getModuleName() { return moduleName; }
    public int getOrderIndex() { return orderIndex; }
}

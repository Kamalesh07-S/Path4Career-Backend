package com.careerpath.admin.dto;

import java.util.List;
import java.time.LocalDateTime;


public class ModuleResponse {
    public Long id;
    public String name;
    public String description;
    public Integer durationValue;
    public String durationUnit;
    public String difficulty;
    public List<SkillMini> skills;
    
    public boolean active;
    public LocalDateTime createdAt;
    public LocalDateTime lastModifiedAt;

    public static class SkillMini {
        public Long id;
        public String name;
        public boolean active;

        public SkillMini(Long id, String name, boolean active) {
            this.id = id;
            this.name = name;
            this.active = active;
        }
    }

}

package com.careerpath.admin.dto;
import com.careerpath.admin.entity.Module;
import com.careerpath.admin.entity.Skill;

import java.util.List;

public class ModuleRequest {
    public String name;
    public String description;
    public Integer durationValue;
    public String durationUnit;
    public String difficulty;
    public List<Long> skillIds;
    
    public static ModuleRequest fromEntity(Module m) {
        ModuleRequest r = new ModuleRequest();
        r.name = m.getName();
        r.description = m.getDescription();
        r.durationValue = m.getDurationValue();
        r.durationUnit = m.getDurationUnit().name();
        r.difficulty = m.getDifficulty().name();
        r.skillIds = m.getSkills()
                .stream()
                .map(Skill::getId)
                .toList();
        return r;
    }
}


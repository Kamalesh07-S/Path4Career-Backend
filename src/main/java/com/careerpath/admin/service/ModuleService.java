package com.careerpath.admin.service;

import com.careerpath.admin.dto.ModuleRequest;
import com.careerpath.admin.entity.Module;
import com.careerpath.admin.entity.Skill;
import com.careerpath.admin.entity.DurationUnit;
import com.careerpath.admin.entity.Difficulty;
import com.careerpath.admin.entity.RoadmapModule;
import com.careerpath.admin.repository.ModuleRepo;
import com.careerpath.admin.repository.SkillRepo;
import com.careerpath.admin.repository.RoadmapModuleRepo;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
public class ModuleService {

    private final ModuleRepo moduleRepo;
    private final SkillRepo skillRepo;
    private final RoadmapModuleRepo roadmapModuleRepo;
    private final RoadmapService roadmapService;

    public ModuleService(
            ModuleRepo moduleRepo,
            SkillRepo skillRepo,
            RoadmapModuleRepo roadmapModuleRepo,
            RoadmapService roadmapService
    ) {
        this.moduleRepo = moduleRepo;
        this.skillRepo = skillRepo;
        this.roadmapModuleRepo = roadmapModuleRepo;
        this.roadmapService = roadmapService;
    }

    public Module create(ModuleRequest r) {
        Module m = new Module();
        apply(m, r);
        return moduleRepo.save(m);
    }

    public Module get(Long id) {
        return moduleRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Module not found"));
    }

    public Module update(Long id, ModuleRequest r) {
        Module m = get(id);
        apply(m, r);
        Module saved = moduleRepo.save(m);

        // 🔑 IMPORTANT: recalculate all affected roadmaps
        List<RoadmapModule> rms =
                roadmapModuleRepo.findByModuleId(saved.getId());

        for (RoadmapModule rm : rms) {
            roadmapService.recalculateRoadmap(rm.getRoadmap());
        }

        return saved;
    }

    public List<Module> getAll() {
        return moduleRepo.findAll();
    }

    private void apply(Module m, ModuleRequest r) {
        m.setName(r.name);
        m.setDescription(r.description);
        m.setDurationValue(r.durationValue);
        m.setDifficulty(Difficulty.valueOf(r.difficulty.toUpperCase()));
        m.setDurationUnit(DurationUnit.valueOf(r.durationUnit.toUpperCase()));

        List<Skill> skills = skillRepo.findAllById(r.skillIds);
        m.setSkills(skills);
    }
    

}

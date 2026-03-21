package com.careerpath.admin.service;

import com.careerpath.admin.entity.Roadmap;
import com.careerpath.admin.entity.RoadmapModule;
import com.careerpath.admin.entity.Module;
import com.careerpath.admin.entity.DurationUnit;
import com.careerpath.admin.entity.Difficulty;
import com.careerpath.admin.repository.RoadmapRepo;
import com.careerpath.admin.repository.RoadmapModuleRepo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoadmapService {

    private final RoadmapRepo roadmapRepo;
    private final RoadmapModuleRepo roadmapModuleRepo;

    public RoadmapService(RoadmapRepo roadmapRepo,
                          RoadmapModuleRepo roadmapModuleRepo) {
        this.roadmapRepo = roadmapRepo;
        this.roadmapModuleRepo = roadmapModuleRepo;
    }

    public void recalculateRoadmap(Roadmap roadmap) {

        int totalHours = 0;
        Difficulty finalDifficulty = Difficulty.BEGINNER;

        List<RoadmapModule> rms =
                roadmapModuleRepo.findByRoadmapIdOrderByOrderIndexAsc(roadmap.getId());

        for (RoadmapModule rm : rms) {
            Module module = rm.getModule();

            if (module.getDurationValue() != null && module.getDurationUnit() != null) {
                int hours = switch (module.getDurationUnit()) {
                    case HOURS -> module.getDurationValue();
                    case DAYS -> module.getDurationValue() * 8;
                    case WEEKS -> module.getDurationValue() * 40;
                };
                totalHours += hours;
            }

            if (module.getDifficulty() == Difficulty.ADVANCED) {
                finalDifficulty = Difficulty.ADVANCED;
            } else if (
                module.getDifficulty() == Difficulty.INTERMEDIATE &&
                finalDifficulty != Difficulty.ADVANCED
            ) {
                finalDifficulty = Difficulty.INTERMEDIATE;
            }
        }

        roadmap.setDurationValue(totalHours);
        roadmap.setDurationUnit(DurationUnit.HOURS);
        roadmap.setDifficulty(finalDifficulty);

        roadmapRepo.save(roadmap);
    }
}

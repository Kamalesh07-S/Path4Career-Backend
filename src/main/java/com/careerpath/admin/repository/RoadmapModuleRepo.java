package com.careerpath.admin.repository;

import com.careerpath.admin.entity.RoadmapModule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoadmapModuleRepo extends JpaRepository<RoadmapModule, Long> {

    List<RoadmapModule> findByRoadmapIdOrderByOrderIndexAsc(Long roadmapId);
    
    List<RoadmapModule> findByModuleId(Long moduleId);

    void deleteByRoadmapId(Long roadmapId);
    
    boolean existsByRoadmapIdAndModuleId(Long roadmapId, Long moduleId);

}

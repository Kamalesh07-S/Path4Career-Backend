package com.careerpath.admin.repository;

import com.careerpath.admin.entity.Roadmap;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoadmapRepo extends JpaRepository<Roadmap, Long> {

    List<Roadmap> findByActiveTrue();

    List<Roadmap> findByPublishedTrueAndActiveTrue();
    
    Optional<Roadmap> findByTitleIgnoreCaseAndActiveTrue(String title);
}

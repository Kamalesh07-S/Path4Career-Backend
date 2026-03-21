package com.careerpath.admin.controller;

import com.careerpath.admin.dto.UserModuleDetailDTO;
import com.careerpath.admin.dto.UserModuleSummaryDTO;
import com.careerpath.admin.dto.UserRoadmapDetailDTO;
import com.careerpath.admin.dto.UserRoadmapSummaryDTO;
import com.careerpath.admin.dto.UserSkillDTO;
import com.careerpath.admin.entity.Module;
import com.careerpath.admin.entity.Roadmap;
import com.careerpath.admin.repository.ModuleRepo;
import com.careerpath.admin.repository.RoadmapModuleRepo;
import com.careerpath.admin.repository.RoadmapRepo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * User-facing read-only API for Roadmap → Module → Skills/Tutorials.
 *
 * All endpoints require an authenticated USER (or any admin role) JWT token.
 * Only published + active roadmaps are visible.
 */
@RestController
@RequestMapping("/api/user/roadmaps")
@CrossOrigin
public class UserRoadmapController {

        private final RoadmapRepo roadmapRepo;
        private final RoadmapModuleRepo roadmapModuleRepo;
        private final ModuleRepo moduleRepo;

        public UserRoadmapController(RoadmapRepo roadmapRepo,
                        RoadmapModuleRepo roadmapModuleRepo,
                        ModuleRepo moduleRepo) {
                this.roadmapRepo = roadmapRepo;
                this.roadmapModuleRepo = roadmapModuleRepo;
                this.moduleRepo = moduleRepo;
        }

        /*
         * ===============================================================
         * 1. GET ALL PUBLISHED & ACTIVE ROADMAPS (summary list)
         * GET /api/user/roadmaps
         * ===============================================================
         */
        @GetMapping
        public List<UserRoadmapSummaryDTO> getPublishedRoadmaps() {

                return roadmapRepo.findAll()
                                .stream()
                                .filter(r -> r.isPublished() && r.isActive())
                                .map(r -> new UserRoadmapSummaryDTO(
                                                r.getId(),
                                                r.getTitle(),
                                                r.getJobRole().getName(),
                                                r.getDifficulty() != null ? r.getDifficulty().name() : null,
                                                r.getDurationValue(),
                                                r.getDurationUnit() != null ? r.getDurationUnit().name() : null))
                                .collect(Collectors.toList());
        }

        /*
         * ===============================================================
         * 2. GET SINGLE ROADMAP DETAIL (with ordered modules)
         * GET /api/user/roadmaps/{id}
         * ===============================================================
         */
        @GetMapping("/{id}")
        public ResponseEntity<?> getRoadmapDetail(@PathVariable Long id) {

                Roadmap roadmap = roadmapRepo.findById(id)
                                .filter(r -> r.isPublished() && r.isActive())
                                .orElse(null);

                if (roadmap == null) {
                        return ResponseEntity.notFound().build();
                }

                List<UserModuleSummaryDTO> modules = roadmapModuleRepo.findByRoadmapIdOrderByOrderIndexAsc(id)
                                .stream()
                                .filter(rm -> rm.getModule().isActive())
                                .map(rm -> new UserModuleSummaryDTO(
                                                rm.getModule().getId(),
                                                rm.getModule().getName(),
                                                rm.getOrderIndex(),
                                                rm.getModule().getDifficulty() != null
                                                                ? rm.getModule().getDifficulty().name()
                                                                : null,
                                                rm.getModule().getDurationValue(),
                                                rm.getModule().getDurationUnit() != null
                                                                ? rm.getModule().getDurationUnit().name()
                                                                : null,
                                                rm.getModule().getSkills().stream().filter(s -> s.isActive())
                                                                .map(s -> s.getId()).collect(Collectors.toList())))
                                .collect(Collectors.toList());

                UserRoadmapDetailDTO dto = new UserRoadmapDetailDTO();
                dto.setId(roadmap.getId());
                dto.setTitle(roadmap.getTitle());
                dto.setDescription(roadmap.getDescription());
                dto.setJobRole(roadmap.getJobRole().getName());
                dto.setDifficulty(roadmap.getDifficulty() != null ? roadmap.getDifficulty().name() : null);
                dto.setDurationValue(roadmap.getDurationValue());
                dto.setDurationUnit(roadmap.getDurationUnit() != null ? roadmap.getDurationUnit().name() : null);
                dto.setModules(modules);

                return ResponseEntity.ok(dto);
        }

        /*
         * ===============================================================
         * 3. GET MODULE DETAIL (with skills + tutorialId)
         * GET /api/user/roadmaps/{roadmapId}/modules/{moduleId}
         * ===============================================================
         */
        @GetMapping("/{roadmapId}/modules/{moduleId}")
        public ResponseEntity<?> getModuleDetail(@PathVariable Long roadmapId,
                        @PathVariable Long moduleId) {

                // Validate the roadmap exists and is published
                boolean roadmapValid = roadmapRepo.findById(roadmapId)
                                .map(r -> r.isPublished() && r.isActive())
                                .orElse(false);

                if (!roadmapValid) {
                        return ResponseEntity.notFound().build();
                }

                // Validate the module belongs to this roadmap
                boolean moduleInRoadmap = roadmapModuleRepo
                                .findByRoadmapIdOrderByOrderIndexAsc(roadmapId)
                                .stream()
                                .anyMatch(rm -> rm.getModule().getId().equals(moduleId));

                if (!moduleInRoadmap) {
                        return ResponseEntity.notFound().build();
                }

                Module module = moduleRepo.findById(moduleId)
                                .filter(Module::isActive)
                                .orElse(null);

                if (module == null) {
                        return ResponseEntity.notFound().build();
                }

                List<UserSkillDTO> skills = module.getSkills()
                                .stream()
                                .filter(s -> s.isActive())
                                .map(s -> new UserSkillDTO(
                                                s.getId(),
                                                s.getName(),
                                                s.getCategory(),
                                                s.getLevel(),
                                                s.getDescription(),
                                                s.getTutorialId() // ← links to frontend JSON tutorial file
                                ))
                                .collect(Collectors.toList());

                UserModuleDetailDTO dto = new UserModuleDetailDTO();
                dto.setId(module.getId());
                dto.setName(module.getName());
                dto.setDescription(module.getDescription());
                dto.setDifficulty(module.getDifficulty() != null ? module.getDifficulty().name() : null);
                dto.setDurationValue(module.getDurationValue());
                dto.setDurationUnit(module.getDurationUnit() != null ? module.getDurationUnit().name() : null);
                dto.setSkills(skills);

                return ResponseEntity.ok(dto);
        }
}

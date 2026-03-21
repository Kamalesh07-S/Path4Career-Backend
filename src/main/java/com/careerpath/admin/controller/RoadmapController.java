package com.careerpath.admin.controller;

import com.careerpath.admin.entity.JobRole;
import com.careerpath.admin.entity.Roadmap;
import com.careerpath.admin.dto.RoadmapModuleResponseDTO;
import com.careerpath.admin.dto.RoadmapListResponseDTO;
import com.careerpath.admin.dto.RoadmapDetailsDTO;
import com.careerpath.admin.dto.RoadmapModuleDTO;
import com.careerpath.admin.repository.JobRoleRepo;
import com.careerpath.admin.repository.RoadmapModuleRepo;
import com.careerpath.admin.repository.RoadmapRepo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.careerpath.admin.entity.Module;
import com.careerpath.admin.entity.RoadmapModule;
import com.careerpath.admin.repository.ModuleRepo;
import com.careerpath.admin.service.RoadmapService;
import java.util.stream.Collectors;	

import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/roadmaps")
@CrossOrigin


public class RoadmapController {

    private final RoadmapRepo roadmapRepo;
    private final JobRoleRepo jobRoleRepo;
    private final RoadmapModuleRepo roadmapModuleRepo;
    private final ModuleRepo moduleRepo;
    private final RoadmapService roadmapService;

    public RoadmapController(RoadmapRepo roadmapRepo,
				            JobRoleRepo jobRoleRepo,
				            RoadmapModuleRepo roadmapModuleRepo,
				            ModuleRepo moduleRepo,
				            RoadmapService roadmapService) {

		this.roadmapRepo = roadmapRepo;
		this.jobRoleRepo = jobRoleRepo;
		this.roadmapModuleRepo = roadmapModuleRepo;
		this.moduleRepo = moduleRepo;
		this.roadmapService = roadmapService;
		}

    /* ===============================
       GET ALL ACTIVE ROADMAPS
       =============================== */
    @GetMapping
    public List<RoadmapListResponseDTO> getAllRoadmaps() {

        return roadmapRepo.findAll()
                .stream()
                .map(r -> new RoadmapListResponseDTO(
                        r.getId(),
                        r.getTitle(),
                        r.getJobRole().getName(),
                        r.isPublished(),
                        r.isActive()
                ))
                .collect(Collectors.toList());
    }
    
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','CONTENT_ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getRoadmapDetails(@PathVariable Long id) {

        Roadmap roadmap = roadmapRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Roadmap not found"));

        RoadmapDetailsDTO dto = new RoadmapDetailsDTO();
        dto.setId(roadmap.getId());
        dto.setTitle(roadmap.getTitle());
        dto.setDescription(roadmap.getDescription());
        dto.setActive(roadmap.isActive());
        dto.setPublished(roadmap.isPublished());
        dto.setCreatedAt(roadmap.getCreatedAt());
        dto.setLastModifiedAt(roadmap.getLastModifiedAt());

        // aggregated fields (null for now)
        dto.setDurationValue(roadmap.getDurationValue());
        dto.setDurationUnit(roadmap.getDurationUnit());
        dto.setDifficulty(roadmap.getDifficulty());

        List<RoadmapModuleDTO> moduleDTOs =
                roadmapModuleRepo.findByRoadmapIdOrderByOrderIndexAsc(id)
                        .stream()
                        .map(rm -> {
                            RoadmapModuleDTO m = new RoadmapModuleDTO();
                            m.setModuleId(rm.getModule().getId());
                            m.setModuleName(rm.getModule().getName());
                            m.setOrderIndex(rm.getOrderIndex());

                            m.setDurationValue(rm.getModule().getDurationValue());
                            m.setDurationUnit(rm.getModule().getDurationUnit());
                            m.setDifficulty(rm.getModule().getDifficulty());
                            m.setActive(rm.getModule().isActive());

                            return m;
                        })
                        .toList();

        dto.setModules(moduleDTOs);

        return ResponseEntity.ok(dto);
    }

    /* ===============================
       CREATE ROADMAP (DRAFT)
       =============================== */
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','CONTENT_ADMIN')")
    @PostMapping
    public ResponseEntity<?> createRoadmap(@RequestBody Roadmap roadmap) {

        if (roadmap.getTitle() == null || roadmap.getTitle().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Roadmap title is required");
        }

        if (roadmap.getJobRole() == null || roadmap.getJobRole().getId() == null) {
            return ResponseEntity.badRequest().body("Job role is required");
        }

        JobRole role = jobRoleRepo.findById(roadmap.getJobRole().getId())
                .orElseThrow(() -> new RuntimeException("Job role not found"));
        
        if (roadmapRepo.findByTitleIgnoreCaseAndActiveTrue(
                roadmap.getTitle().trim()
            ).isPresent()) {

            return ResponseEntity.badRequest()
                    .body("Roadmap with this title already exists");
        }

        roadmap.setJobRole(role);
        roadmap.setPublished(false);
        roadmap.setActive(true);
        
        Roadmap saved = roadmapRepo.save(roadmap);
        return ResponseEntity.ok(saved);
    }
    
    /* ===============================
    UPDATE ROADMAP (METADATA ONLY)
    =============================== */
     @PreAuthorize("hasAnyRole('SUPER_ADMIN','CONTENT_ADMIN')")
	 @PutMapping("/{id}")
	 public ResponseEntity<?> updateRoadmap(
	         @PathVariable Long id,
	         @RequestBody Roadmap updated
	 ) {
	
	     Roadmap roadmap = roadmapRepo.findById(id)
	             .orElseThrow(() -> new RuntimeException("Roadmap not found"));
	
	     // ---- validation ----
	     if (updated.getTitle() == null || updated.getTitle().trim().isEmpty()) {
	         return ResponseEntity.badRequest().body("Roadmap title is required");
	     }
	
	     if (updated.getJobRole() == null || updated.getJobRole().getId() == null) {
	         return ResponseEntity.badRequest().body("Job role is required");
	     }
	
	     // ---- duplicate title check (exclude current roadmap) ----
	     roadmapRepo.findByTitleIgnoreCaseAndActiveTrue(updated.getTitle().trim())
	             .ifPresent(existing -> {
	                 if (!existing.getId().equals(id)) {
	                     throw new RuntimeException("Roadmap with this title already exists");
	                 }
	             });
	
	     JobRole role = jobRoleRepo.findById(updated.getJobRole().getId())
	             .orElseThrow(() -> new RuntimeException("Job role not found"));
	
	     // ---- update fields ----
	     roadmap.setTitle(updated.getTitle().trim());
	     roadmap.setDescription(updated.getDescription());
	     roadmap.setJobRole(role);
	     
	     roadmapService.recalculateRoadmap(roadmap);
	     
	     roadmapRepo.save(roadmap);
	     return ResponseEntity.ok("Roadmap updated successfully");
	 }

    /* ===============================
       PUBLISH ROADMAP
       =============================== */
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','CONTENT_ADMIN')")
    @PutMapping("/{id}/publish")
    public ResponseEntity<?> publishRoadmap(@PathVariable Long id) {

        Roadmap roadmap = roadmapRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Roadmap not found"));

        long moduleCount = roadmapModuleRepo
                .findByRoadmapIdOrderByOrderIndexAsc(id)
                .size();

        if (moduleCount == 0) {
            return ResponseEntity.badRequest()
                    .body("Cannot publish roadmap without modules");
        }

        roadmap.setPublished(true);
        roadmapRepo.save(roadmap);

        return ResponseEntity.ok("Roadmap published");
    }

    /* ===============================
       UNPUBLISH ROADMAP
       =============================== */
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','CONTENT_ADMIN')")
    @PutMapping("/{id}/unpublish")
    public ResponseEntity<?> unpublishRoadmap(@PathVariable Long id) {

        Roadmap roadmap = roadmapRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Roadmap not found"));

        roadmap.setPublished(false);
        roadmapRepo.save(roadmap);

        return ResponseEntity.ok("Roadmap unpublished");
    }

    /* ===============================
       SOFT DELETE ROADMAP
       =============================== */
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','CONTENT_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRoadmap(@PathVariable Long id) {

        Roadmap roadmap = roadmapRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Roadmap not found"));

        roadmap.setActive(false);
        //roadmapModuleRepo.deleteByRoadmapId(id);
        roadmapRepo.save(roadmap);

        return ResponseEntity.ok("Roadmap deleted");
    }
    
    /* Activate roadmap */
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','CONTENT_ADMIN')")
    @PutMapping("/{id}/activate")
    public ResponseEntity<?> activateRoadmap(@PathVariable Long id) {

        Roadmap roadmap = roadmapRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Roadmap not found"));

        roadmap.setActive(true);
        roadmapRepo.save(roadmap);

        return ResponseEntity.ok("Roadmap activated");
    }


    /* add module to raodmap */
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','CONTENT_ADMIN')")
    @PostMapping("/{roadmapId}/modules")
    public ResponseEntity<?> addModuleToRoadmap(@PathVariable Long roadmapId,
                                                @RequestParam Long moduleId) {

        if (roadmapModuleRepo.existsByRoadmapIdAndModuleId(roadmapId, moduleId)) {
            return ResponseEntity.badRequest()
                    .body("Module already added to this roadmap");
        }

        Roadmap roadmap = roadmapRepo.findById(roadmapId)
                .orElseThrow(() -> new RuntimeException("Roadmap not found"));

        Module module = moduleRepo.findById(moduleId)
                .orElseThrow(() -> new RuntimeException("Module not found"));

        int nextOrder = roadmapModuleRepo
                .findByRoadmapIdOrderByOrderIndexAsc(roadmapId)
                .size() + 1;

        RoadmapModule rm = new RoadmapModule();
        rm.setRoadmap(roadmap);
        rm.setModule(module);
        rm.setOrderIndex(nextOrder);

        roadmapModuleRepo.save(rm);
        roadmapService.recalculateRoadmap(roadmap);
        
        return ResponseEntity.ok("Module added to roadmap");
    }
    
    /* list modules */
    @GetMapping("/{roadmapId}/modules")
    public List<RoadmapModuleResponseDTO> getRoadmapModules(@PathVariable Long roadmapId) {

        return roadmapModuleRepo
                .findByRoadmapIdOrderByOrderIndexAsc(roadmapId)
                .stream()
                .map(rm -> new RoadmapModuleResponseDTO(
                        rm.getId(),
                        rm.getModule().getId(),
                        rm.getModule().getName(),
                        rm.getOrderIndex()
                ))
                .collect(Collectors.toList());
    }


    /* remove module from roadmap */
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','CONTENT_ADMIN')")
    @DeleteMapping("/{roadmapId}/modules/{moduleId}")
    public ResponseEntity<?> removeModuleFromRoadmap(@PathVariable Long roadmapId,
                                                     @PathVariable Long moduleId) {

        List<RoadmapModule> rms =
                roadmapModuleRepo.findByRoadmapIdOrderByOrderIndexAsc(roadmapId);

        rms.stream()
           .filter(rm -> rm.getModule().getId().equals(moduleId))
           .findFirst()
           .ifPresent(roadmapModuleRepo::delete);
        
        Roadmap roadmap = roadmapRepo.findById(roadmapId)
                .orElseThrow(() -> new RuntimeException("Roadmap not found"));

        roadmapService.recalculateRoadmap(roadmap);
        roadmapRepo.save(roadmap);


        return ResponseEntity.ok("Module removed from roadmap");
    }
    
    
}

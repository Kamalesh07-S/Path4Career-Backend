package com.careerpath.admin.controller;

import com.careerpath.admin.dto.UserModuleDetailDTO;
import com.careerpath.admin.dto.UserSkillDTO;
import com.careerpath.admin.entity.Module;
import com.careerpath.admin.repository.ModuleRepo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * User-facing read-only API for Courses (Modules) → Skills/Tutorials.
 *
 * From the user's perspective, modules ARE courses.
 * No roadmap layer is exposed.
 */
@RestController
@RequestMapping("/api/user/modules")
@CrossOrigin
public class UserModuleController {

    private final ModuleRepo moduleRepo;

    public UserModuleController(ModuleRepo moduleRepo) {
        this.moduleRepo = moduleRepo;
    }

    /**
     * GET /api/user/modules
     * Lists all active modules as "courses" for the user.
     */
    @GetMapping
    public List<UserCourseSummaryDTO> getAllCourses() {
        return moduleRepo.findAll()
                .stream()
                .filter(Module::isActive)
                .map(m -> new UserCourseSummaryDTO(
                        m.getId(),
                        m.getName(),
                        m.getDescription(),
                        m.getDifficulty() != null ? m.getDifficulty().name() : null,
                        m.getDurationValue(),
                        m.getDurationUnit() != null ? m.getDurationUnit().name() : null,
                        m.getSkills().stream()
                                .filter(s -> s.isActive())
                                .map(s -> s.getId())
                                .collect(Collectors.toList())))
                .collect(Collectors.toList());
    }

    /**
     * GET /api/user/modules/{id}
     * Returns module detail with its skills (for the skills page).
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getCourseDetail(@PathVariable Long id) {
        Module module = moduleRepo.findById(id)
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
                        s.getTutorialId()))
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

    /**
     * Lightweight DTO for course list view.
     */
    public static class UserCourseSummaryDTO {
        private Long id;
        private String name;
        private String description;
        private String difficulty;
        private Integer durationValue;
        private String durationUnit;
        private List<Long> skillIds;

        public UserCourseSummaryDTO() {
        }

        public UserCourseSummaryDTO(Long id, String name, String description,
                String difficulty, Integer durationValue, String durationUnit, List<Long> skillIds) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.difficulty = difficulty;
            this.durationValue = durationValue;
            this.durationUnit = durationUnit;
            this.skillIds = skillIds;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getDifficulty() {
            return difficulty;
        }

        public void setDifficulty(String difficulty) {
            this.difficulty = difficulty;
        }

        public Integer getDurationValue() {
            return durationValue;
        }

        public void setDurationValue(Integer durationValue) {
            this.durationValue = durationValue;
        }

        public String getDurationUnit() {
            return durationUnit;
        }

        public void setDurationUnit(String durationUnit) {
            this.durationUnit = durationUnit;
        }

        public List<Long> getSkillIds() {
            return skillIds;
        }

        public void setSkillIds(List<Long> skillIds) {
            this.skillIds = skillIds;
        }
    }
}

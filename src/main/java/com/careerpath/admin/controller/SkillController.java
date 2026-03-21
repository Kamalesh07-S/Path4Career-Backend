package com.careerpath.admin.controller;

import com.careerpath.admin.entity.Skill;
import com.careerpath.admin.repository.SkillRepo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/skills")
@CrossOrigin
public class SkillController {

    private final SkillRepo skillRepo;

    public SkillController(SkillRepo skillRepo) {
        this.skillRepo = skillRepo;
    }

    // 1. Get all skills
    @GetMapping
    public List<Skill> getAllSkills() {
        return skillRepo.findAll();
    }

    // 2. Create new skill (SUPER_ADMIN / CONTENT_ADMIN later via RBAC)
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','CONTENT_ADMIN')")
    @PostMapping
    public ResponseEntity<?> addSkill(@RequestBody Skill skill) {

        if (skill.getName() == null || skill.getName().isEmpty() ||
                skill.getCategory() == null || skill.getCategory().isEmpty() ||
                skill.getLevel() == null || skill.getLevel().isEmpty() ||
                skill.getDescription() == null || skill.getDescription().isEmpty()) {

            return ResponseEntity.badRequest().body("All fields are required");
        }

        if (skillRepo.existsByNameAndCategoryAndLevel(
                skill.getName(),
                skill.getCategory(),
                skill.getLevel())) {

            return ResponseEntity
                    .badRequest()
                    .body("Skill already exists");
        }

        skillRepo.save(skill);
        return ResponseEntity.ok(skill);
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN','CONTENT_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateSkill(
            @PathVariable Long id,
            @RequestBody Skill updatedSkill) {
        Skill existing = skillRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Skill not found"));

        // Duplicate check (same rule as create)
        boolean duplicateExists = skillRepo.existsByNameAndCategoryAndLevel(
                updatedSkill.getName(),
                updatedSkill.getCategory(),
                updatedSkill.getLevel());

        if (duplicateExists &&
                !(existing.getName().equalsIgnoreCase(updatedSkill.getName())
                        && existing.getCategory().equalsIgnoreCase(updatedSkill.getCategory())
                        && existing.getLevel().equalsIgnoreCase(updatedSkill.getLevel()))) {
            return ResponseEntity
                    .badRequest()
                    .body("Duplicate skill already exists");

        }

        existing.setName(updatedSkill.getName());
        existing.setCategory(updatedSkill.getCategory());
        existing.setLevel(updatedSkill.getLevel());
        existing.setDescription(updatedSkill.getDescription());
        existing.setTutorialId(updatedSkill.getTutorialId());

        return ResponseEntity.ok(skillRepo.save(existing));
    }

    // 3. Enable / Disable skill
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','CONTENT_ADMIN')")
    @PutMapping("/{id}/toggle")
    public ResponseEntity<?> toggleSkill(@PathVariable Long id) {

        Skill skill = skillRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Skill not found"));

        skill.setActive(!skill.isActive());
        skillRepo.save(skill);

        return ResponseEntity.ok(skill);
    }
}

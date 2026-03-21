package com.careerpath.admin.controller;

import com.careerpath.admin.entity.JobRole;
import com.careerpath.admin.repository.JobRoleRepo;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.stream.Collectors;
import java.util.List;

@RestController
@RequestMapping("/api/job-roles")
@CrossOrigin
public class JobRoleController {

    private final JobRoleRepo jobRoleRepo;

    public JobRoleController(JobRoleRepo jobRoleRepo) {
        this.jobRoleRepo = jobRoleRepo;
    }

    /*
     * ===============================
     * GET ALL ACTIVE JOB ROLES
     * (All authenticated admins)
     * ===============================
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','JOB_ADMIN','MODULE_ADMIN','ROADMAP_ADMIN')")
    public List<JobRole> getAllJobRoles() {
        return jobRoleRepo.findAll()
                .stream()
                .filter(JobRole::isActive)
                .collect(Collectors.toList());
    }

    /**
     * Public endpoint for listing active job roles (used by recruiter job form).
     */
    @GetMapping("/public")
    public List<JobRole> getPublicJobRoles() {
        return jobRoleRepo.findAll()
                .stream()
                .filter(JobRole::isActive)
                .collect(Collectors.toList());
    }

    /*
     * ===============================
     * CREATE JOB ROLE
     * (SUPER_ADMIN & JOB_ADMIN only)
     * ===============================
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','JOB_ADMIN')")
    public ResponseEntity<?> createJobRole(@RequestBody JobRole role) {

        if (role.getName() == null || role.getName().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Job role name is required");
        }

        if (jobRoleRepo.findByNameIgnoreCase(role.getName()).isPresent()) {
            return ResponseEntity.badRequest().body("Job role already exists");
        }

        JobRole saved = jobRoleRepo.save(role);
        return ResponseEntity.ok(saved);
    }

    /*
     * ===============================
     * ACTIVATE / DEACTIVATE
     * (SUPER_ADMIN & JOB_ADMIN only)
     * ===============================
     */
    @PutMapping("/{id}/toggle")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','JOB_ADMIN')")
    public ResponseEntity<?> toggleJobRole(@PathVariable Long id) {

        JobRole role = jobRoleRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Job role not found"));

        role.setActive(!role.isActive());
        jobRoleRepo.save(role);

        return ResponseEntity.ok(
                role.isActive()
                        ? "Job role activated"
                        : "Job role deactivated");
    }
}

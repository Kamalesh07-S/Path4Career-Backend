package com.careerpath.admin.controller;

import com.careerpath.admin.dto.JobCreateRequest;
import com.careerpath.admin.dto.JobResponse;
import com.careerpath.admin.dto.JobUpdateRequest;
import com.careerpath.admin.entity.User;
import com.careerpath.admin.repository.UserRepo;
import com.careerpath.admin.service.JobService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
@CrossOrigin

public class JobController {

    private final JobService jobService;
    private final UserRepo userRepo;

    public JobController(JobService jobService, UserRepo userRepo) {
        this.jobService = jobService;
        this.userRepo = userRepo;
    }

    // ===== Admin endpoints =====

    @PreAuthorize("hasAnyRole('SUPER_ADMIN','JOB_ADMIN')")
    @PostMapping
    public ResponseEntity<?> createJob(@RequestBody JobCreateRequest request) {
        try {
            return ResponseEntity.ok(jobService.createJob(request));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN','JOB_ADMIN')")
    @PutMapping("/{id}/approve")
    public ResponseEntity<?> approveJob(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(jobService.approveJob(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN','JOB_ADMIN')")
    @PutMapping("/{id}/reject")
    public ResponseEntity<?> rejectJob(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(jobService.rejectJob(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN','JOB_ADMIN')")
    @PutMapping("/{id}/revoke")
    public ResponseEntity<?> revokeJob(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(jobService.revokeJob(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN','JOB_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateJob(@PathVariable Long id, @RequestBody JobUpdateRequest request) {
        try {
            return ResponseEntity.ok(jobService.updateJob(id, request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN','JOB_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteJob(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(jobService.softDeleteJob(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN','JOB_ADMIN')")
    @GetMapping("/pending")
    public ResponseEntity<List<JobResponse>> getPendingJobs() {
        return ResponseEntity.ok(jobService.getPendingJobs());
    }

    // ===== Public endpoints =====

    @GetMapping
    public ResponseEntity<List<JobResponse>> getApprovedJobs() {
        return ResponseEntity.ok(jobService.getApprovedJobsForUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getJobById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(jobService.getJobById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ===== Recruiter endpoints =====

    @PostMapping("/recruiter/post")
    public ResponseEntity<?> recruiterPostJob(
            @RequestBody JobCreateRequest request,
            Authentication authentication) {
        try {
            User recruiter = getAuthenticatedUser(authentication);
            return ResponseEntity.ok(jobService.createRecruiterJob(request, recruiter));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/recruiter/my-jobs")
    public ResponseEntity<?> recruiterGetMyJobs(Authentication authentication) {
        try {
            User recruiter = getAuthenticatedUser(authentication);
            return ResponseEntity.ok(jobService.getJobsByRecruiter(recruiter.getId()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/recruiter/{id}")
    public ResponseEntity<?> recruiterUpdateJob(
            @PathVariable Long id,
            @RequestBody JobUpdateRequest request,
            Authentication authentication) {
        try {
            getAuthenticatedUser(authentication); // ensure authenticated
            return ResponseEntity.ok(jobService.updateJob(id, request));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/close")
    public ResponseEntity<?> closeJob(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(jobService.closeJob(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/recruiter/{id}")
    public ResponseEntity<?> recruiterDeleteJob(
            @PathVariable Long id,
            Authentication authentication) {
        try {
            getAuthenticatedUser(authentication); // ensure authenticated
            return ResponseEntity.ok(jobService.softDeleteJob(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private User getAuthenticatedUser(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new RuntimeException("Authentication required");
        }
        User user = userRepo.findByEmail(authentication.getName());
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        if (!"RECRUITER".equalsIgnoreCase(user.getRole())) {
            throw new RuntimeException("Access denied. Only recruiters can perform this action.");
        }
        return user;
    }
}

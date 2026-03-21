package com.careerpath.admin.controller;

import com.careerpath.admin.dto.JobApplicationRequest;
import com.careerpath.admin.dto.JobApplicationResponse;
import com.careerpath.admin.entity.User;
import com.careerpath.admin.repository.UserRepo;
import com.careerpath.admin.service.JobApplicationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/applications")
@CrossOrigin
public class JobApplicationController {

    private final JobApplicationService applicationService;
    private final UserRepo userRepo;

    public JobApplicationController(JobApplicationService applicationService, UserRepo userRepo) {
        this.applicationService = applicationService;
        this.userRepo = userRepo;
    }

    /**
     * Apply to a job. Works for both authenticated and unauthenticated users.
     */
    @PostMapping("/job/{jobId}/apply")
    public ResponseEntity<?> applyToJob(
            @PathVariable Long jobId,
            @RequestBody JobApplicationRequest request,
            Authentication authentication) {
        try {
            User user = null;
            if (authentication != null && authentication.getName() != null) {
                user = userRepo.findByEmail(authentication.getName());
            }
            JobApplicationResponse response = applicationService.apply(jobId, request, user);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    /**
     * Get logged-in user's applications.
     */
    @GetMapping("/my")
    public ResponseEntity<?> getMyApplications(Authentication authentication) {
        try {
            if (authentication == null || authentication.getName() == null) {
                return ResponseEntity.status(401).body("Authentication required");
            }
            User user = userRepo.findByEmail(authentication.getName());
            if (user == null) {
                return ResponseEntity.badRequest().body("User not found");
            }
            return ResponseEntity.ok(applicationService.getApplicationsByUserId(user.getId()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    /**
     * Get applications by email (public — for non-logged-in users tracking their applications).
     */
    @GetMapping("/by-email")
    public ResponseEntity<List<JobApplicationResponse>> getApplicationsByEmail(
            @RequestParam String email) {
        return ResponseEntity.ok(applicationService.getApplicationsByEmail(email));
    }

    /**
     * Get applicants for a specific job (recruiter view).
     */
    @GetMapping("/job/{jobId}")
    public ResponseEntity<List<JobApplicationResponse>> getApplicantsForJob(
            @PathVariable Long jobId) {
        return ResponseEntity.ok(applicationService.getApplicationsByJobId(jobId));
    }

    /**
     * Update application status (recruiter action).
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateApplicationStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        try {
            String newStatus = body.get("status");
            if (newStatus == null || newStatus.isBlank()) {
                return ResponseEntity.badRequest().body("Status is required");
            }
            return ResponseEntity.ok(applicationService.updateStatus(id, newStatus));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}

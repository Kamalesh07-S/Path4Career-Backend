package com.careerpath.admin.controller;

import com.careerpath.admin.service.AdzunaService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;   

@RestController
@RequestMapping("/api/adzuna-jobs")
@CrossOrigin
public class AdzunaJobController {

    private final AdzunaService adzunaService;

    public AdzunaJobController(AdzunaService adzunaService) {
        this.adzunaService = adzunaService;
    }

    /*
     * ===============================
     * SEARCH JOBS FROM ADZUNA API
     * (SUPER_ADMIN & JOB_ADMIN only)
     * ===============================
     */
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','JOB_ADMIN')")
    public ResponseEntity<?> searchJobs(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int resultsPerPage,
            @RequestParam(defaultValue = "in") String country) {

        try {
            Map<String, Object> results = adzunaService.searchJobs(keyword, page, resultsPerPage, country);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Failed to fetch jobs from Adzuna: " + e.getMessage());
        }
    }
}

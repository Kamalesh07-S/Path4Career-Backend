package com.careerpath.admin.service;

import com.careerpath.admin.dto.JobApplicationRequest;
import com.careerpath.admin.dto.JobApplicationResponse;
import com.careerpath.admin.entity.*;
import com.careerpath.admin.repository.JobApplicationRepo;
import com.careerpath.admin.repository.JobRepo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class JobApplicationService {

    private final JobApplicationRepo applicationRepo;
    private final JobRepo jobRepo;

    public JobApplicationService(JobApplicationRepo applicationRepo, JobRepo jobRepo) {
        this.applicationRepo = applicationRepo;
        this.jobRepo = jobRepo;
    }

    public JobApplicationResponse apply(Long jobId, JobApplicationRequest request, User user) {
        Job job = jobRepo.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        if (job.getStatus() != JobStatus.APPROVED) {
            throw new IllegalArgumentException("This job is no longer accepting applications");
        }

        // Check for duplicate application
        if (applicationRepo.existsByJobIdAndApplicantEmailIgnoreCase(jobId, request.getApplicantEmail())) {
            throw new IllegalArgumentException("You have already applied to this job");
        }

        JobApplication application = new JobApplication();
        application.setJob(job);
        application.setApplicantName(request.getApplicantName());
        application.setApplicantEmail(request.getApplicantEmail());
        application.setApplicantPhone(request.getApplicantPhone());
        application.setPitch(request.getPitch());
        application.setStatus(ApplicationStatus.APPLIED);

        if (user != null) {
            application.setUser(user);
        }

        JobApplication saved = applicationRepo.save(application);

        // Increment application count on the job
        job.setApplicationCount(job.getApplicationCount() + 1);
        jobRepo.save(job);

        return mapToResponse(saved);
    }

    public List<JobApplicationResponse> getApplicationsByEmail(String email) {
        return applicationRepo.findByApplicantEmailIgnoreCase(email)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<JobApplicationResponse> getApplicationsByUserId(Long userId) {
        return applicationRepo.findByUserId(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<JobApplicationResponse> getApplicationsByJobId(Long jobId) {
        return applicationRepo.findByJobId(jobId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public JobApplicationResponse updateStatus(Long applicationId, String newStatus) {
        JobApplication application = applicationRepo.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        try {
            ApplicationStatus status = ApplicationStatus.valueOf(newStatus.toUpperCase());
            application.setStatus(status);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status: " + newStatus);
        }

        return mapToResponse(applicationRepo.save(application));
    }

    private JobApplicationResponse mapToResponse(JobApplication app) {
        JobApplicationResponse response = new JobApplicationResponse();
        response.setId(app.getId());
        response.setJobId(app.getJob().getId());
        response.setJobTitle(app.getJob().getTitle());
        response.setCompany(app.getJob().getCompany());
        response.setApplicantName(app.getApplicantName());
        response.setApplicantEmail(app.getApplicantEmail());
        response.setApplicantPhone(app.getApplicantPhone());
        response.setPitch(app.getPitch());
        response.setStatus(app.getStatus());
        response.setAppliedAt(app.getAppliedAt());
        return response;
    }
}

package com.careerpath.admin.service;

import com.careerpath.admin.dto.JobCreateRequest;
import com.careerpath.admin.dto.JobResponse;
import com.careerpath.admin.dto.JobUpdateRequest;
import com.careerpath.admin.entity.*;
import com.careerpath.admin.repository.JobRepo;
import com.careerpath.admin.repository.JobRoleRepo;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JobService {

    private final JobRepo jobRepo;
    private final JobRoleRepo jobRoleRepo;

    public JobService(JobRepo jobRepo, JobRoleRepo jobRoleRepo) {
        this.jobRepo = jobRepo;
        this.jobRoleRepo = jobRoleRepo;
    }

    public JobResponse createJob(JobCreateRequest request) {

        // Determine source (default: INTERNAL)
        JobSource source = JobSource.INTERNAL;
        if (request.getSource() != null) {
            try {
                source = JobSource.valueOf(request.getSource().toUpperCase());
            } catch (IllegalArgumentException ignored) {
                // fallback to INTERNAL
            }
        }

        String externalId = request.getExternalId();

        // Adzuna duplicate check by externalId
        if (source == JobSource.ADZUNA && externalId != null && !externalId.isBlank()) {
            if (jobRepo.existsByExternalId(externalId)) {
                throw new IllegalArgumentException("This Adzuna job has already been imported");
            }
        }

        // Manual duplicate check (title + company + location)
        boolean duplicateExists = jobRepo
                .existsByTitleIgnoreCaseAndCompanyIgnoreCaseAndLocationIgnoreCaseAndStatusIn(
                        request.getTitle(),
                        request.getCompany(),
                        request.getLocation(),
                        List.of(JobStatus.PENDING, JobStatus.APPROVED));

        if (duplicateExists) {
            throw new IllegalArgumentException("Duplicate job posting already exists");
        }

        JobRole role = jobRoleRepo.findById(request.getJobRoleId())
                .orElseThrow(() -> new RuntimeException("Job Role not found"));

        Job job = new Job();
        job.setTitle(request.getTitle());
        job.setCompany(request.getCompany());
        job.setLocation(request.getLocation());
        job.setDescription(request.getDescription());
        job.setApplyUrl(request.getApplyUrl());
        job.setJobRole(role);
        job.setStatus(JobStatus.PENDING);
        job.setSource(source);
        job.setExpiryDate(request.getExpiryDate());
        job.setViewCount(0);
        job.setApplicationCount(0);
        job.setExternalId(externalId);
        job.setSalary(request.getSalary());
        job.setJobType(request.getJobType());

        Job saved = jobRepo.save(job);

        return mapToResponse(saved);
    }

    /**
     * Recruiter creates a job — sets source to RECRUITER and links to the posting user.
     */
    public JobResponse createRecruiterJob(JobCreateRequest request, User recruiter) {
        JobRole role = jobRoleRepo.findById(request.getJobRoleId())
                .orElseThrow(() -> new RuntimeException("Job Role not found"));

        // Duplicate check
        boolean duplicateExists = jobRepo
                .existsByTitleIgnoreCaseAndCompanyIgnoreCaseAndLocationIgnoreCaseAndStatusIn(
                        request.getTitle(),
                        request.getCompany(),
                        request.getLocation(),
                        List.of(JobStatus.PENDING, JobStatus.APPROVED));

        if (duplicateExists) {
            throw new IllegalArgumentException("Duplicate job posting already exists");
        }

        Job job = new Job();
        job.setTitle(request.getTitle());
        job.setCompany(request.getCompany());
        job.setLocation(request.getLocation());
        job.setDescription(request.getDescription());
        job.setApplyUrl(request.getApplyUrl());
        job.setJobRole(role);
        job.setStatus(JobStatus.APPROVED); // Auto-approve recruiter jobs
        job.setSource(JobSource.RECRUITER);
        job.setExpiryDate(request.getExpiryDate());
        job.setViewCount(0);
        job.setApplicationCount(0);
        job.setSalary(request.getSalary());
        job.setJobType(request.getJobType());
        job.setPostedBy(recruiter);

        Job saved = jobRepo.save(job);
        return mapToResponse(saved);
    }

    public JobResponse approveJob(Long id) {
        Job job = getJobOrThrow(id);
        if (job.getStatus() != JobStatus.PENDING) {
            throw new IllegalArgumentException("Only PENDING jobs can be approved");
        }
        job.setStatus(JobStatus.APPROVED);
        return mapToResponse(jobRepo.save(job));
    }

    public JobResponse rejectJob(Long id) {
        Job job = getJobOrThrow(id);
        if (job.getStatus() != JobStatus.PENDING) {
            throw new IllegalArgumentException("Only PENDING jobs can be rejected");
        }
        job.setStatus(JobStatus.REJECTED);
        return mapToResponse(jobRepo.save(job));
    }

    public JobResponse revokeJob(Long id) {
        Job job = getJobOrThrow(id);
        if (job.getStatus() != JobStatus.APPROVED) {
            throw new IllegalArgumentException("Only APPROVED jobs can be revoked");
        }
        job.setStatus(JobStatus.PENDING);
        return mapToResponse(jobRepo.save(job));
    }

    public JobResponse closeJob(Long id) {
        Job job = getJobOrThrow(id);
        if (job.getStatus() != JobStatus.APPROVED) {
            throw new IllegalArgumentException("Only APPROVED jobs can be closed");
        }
        job.setStatus(JobStatus.CLOSED);
        return mapToResponse(jobRepo.save(job));
    }

    public JobResponse getJobById(Long id) {
        Job job = getJobOrThrow(id);
        return mapToResponse(job);
    }

    public JobResponse updateJob(Long id, JobUpdateRequest request) {
        Job job = getJobOrThrow(id);

        JobRole role = jobRoleRepo.findById(request.getJobRoleId())
                .orElseThrow(() -> new RuntimeException("Job Role not found"));

        job.setTitle(request.getTitle());
        job.setCompany(request.getCompany());
        job.setLocation(request.getLocation());
        job.setDescription(request.getDescription());
        job.setApplyUrl(request.getApplyUrl());
        job.setJobRole(role);
        job.setExpiryDate(request.getExpiryDate());
        job.setSalary(request.getSalary());
        job.setJobType(request.getJobType());

        // Only reset to PENDING for non-recruiter jobs
        if (job.getSource() != JobSource.RECRUITER) {
            job.setStatus(JobStatus.PENDING);
        }

        return mapToResponse(jobRepo.save(job));
    }

    public JobResponse softDeleteJob(Long id) {
        Job job = getJobOrThrow(id);
        job.setStatus(JobStatus.DELETED);
        return mapToResponse(jobRepo.save(job));
    }

    public List<JobResponse> getPendingJobs() {
        return jobRepo.findByStatus(JobStatus.PENDING)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<JobResponse> getApprovedJobsForUsers() {
        return jobRepo.findByStatusAndExpiryDateAfterOrStatusAndExpiryDateIsNull(
                JobStatus.APPROVED,
                LocalDateTime.now(),
                JobStatus.APPROVED)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<JobResponse> getJobsByRecruiter(Long userId) {
        return jobRepo.findByPostedById(userId)
                .stream()
                .filter(j -> j.getStatus() != JobStatus.DELETED)
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private Job getJobOrThrow(Long id) {
        Job job = jobRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found"));
        if (job.getStatus() == JobStatus.DELETED) {
            throw new RuntimeException("Job not found");
        }
        return job;
    }

    private JobResponse mapToResponse(Job job) {

        JobResponse response = new JobResponse();

        response.setId(job.getId());
        response.setTitle(job.getTitle());
        response.setCompany(job.getCompany());
        response.setLocation(job.getLocation());
        response.setDescription(job.getDescription());
        response.setApplyUrl(job.getApplyUrl());
        response.setJobRoleName(job.getJobRole().getName());
        response.setStatus(job.getStatus());
        response.setSource(job.getSource());
        response.setExpiryDate(job.getExpiryDate());
        response.setViewCount(job.getViewCount());
        response.setApplicationCount(job.getApplicationCount());
        response.setCreatedAt(job.getCreatedAt());
        response.setSalary(job.getSalary());
        response.setJobType(job.getJobType());

        if (job.getPostedBy() != null) {
            response.setPostedByUsername(job.getPostedBy().getUsername());
        }

        return response;
    }
}

package com.careerpath.admin.dto;

import com.careerpath.admin.entity.JobStatus;
import com.careerpath.admin.entity.JobSource;

import java.time.LocalDateTime;

public class JobResponse {

    private Long id;
    private String title;
    private String company;
    private String location;
    private String description;
    private String applyUrl;
    private String jobRoleName;
    private JobStatus status;
    private JobSource source;
    private LocalDateTime expiryDate;
    private int viewCount;
    private int applicationCount;
    private LocalDateTime createdAt;
    private String salary;
    private String jobType;
    private String postedByUsername;

    public JobResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getApplyUrl() { return applyUrl; }
    public void setApplyUrl(String applyUrl) { this.applyUrl = applyUrl; }

    public String getJobRoleName() { return jobRoleName; }
    public void setJobRoleName(String jobRoleName) { this.jobRoleName = jobRoleName; }

    public JobStatus getStatus() { return status; }
    public void setStatus(JobStatus status) { this.status = status; }

    public JobSource getSource() { return source; }
    public void setSource(JobSource source) { this.source = source; }

    public LocalDateTime getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDateTime expiryDate) { this.expiryDate = expiryDate; }

    public int getViewCount() { return viewCount; }
    public void setViewCount(int viewCount) { this.viewCount = viewCount; }

    public int getApplicationCount() { return applicationCount; }
    public void setApplicationCount(int applicationCount) { this.applicationCount = applicationCount; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getSalary() { return salary; }
    public void setSalary(String salary) { this.salary = salary; }

    public String getJobType() { return jobType; }
    public void setJobType(String jobType) { this.jobType = jobType; }

    public String getPostedByUsername() { return postedByUsername; }
    public void setPostedByUsername(String postedByUsername) { this.postedByUsername = postedByUsername; }
}


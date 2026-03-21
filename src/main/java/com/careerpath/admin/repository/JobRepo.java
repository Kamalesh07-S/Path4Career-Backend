package com.careerpath.admin.repository;

import com.careerpath.admin.entity.Job;
import com.careerpath.admin.entity.JobStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface JobRepo extends JpaRepository<Job, Long> {

    List<Job> findByStatus(JobStatus status);

    List<Job> findByStatusAndExpiryDateAfter(JobStatus status, LocalDateTime now);

    boolean existsByTitleIgnoreCaseAndCompanyIgnoreCaseAndLocationIgnoreCaseAndStatusIn(
            String title,
            String company,
            String location,
            List<JobStatus> statuses);

    boolean existsByExternalId(String externalId);

    List<Job> findByStatusNot(JobStatus status);

    List<Job> findByPostedById(Long userId);

    List<Job> findByStatusAndExpiryDateAfterOrStatusAndExpiryDateIsNull(
            JobStatus status1, LocalDateTime now,
            JobStatus status2);
}


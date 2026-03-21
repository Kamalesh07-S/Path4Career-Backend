package com.careerpath.admin.repository;

import com.careerpath.admin.entity.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobApplicationRepo extends JpaRepository<JobApplication, Long> {

    List<JobApplication> findByJobId(Long jobId);

    List<JobApplication> findByApplicantEmailIgnoreCase(String email);

    List<JobApplication> findByUserId(Long userId);

    boolean existsByJobIdAndApplicantEmailIgnoreCase(Long jobId, String email);
}

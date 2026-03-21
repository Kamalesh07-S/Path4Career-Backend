package com.careerpath.admin.repository;

import com.careerpath.admin.entity.JobRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JobRoleRepo extends JpaRepository<JobRole, Long> {

    Optional<JobRole> findByNameIgnoreCase(String name);
}

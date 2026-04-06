package com.careerpath.admin.repository;

import com.careerpath.admin.entity.ButtonAnalytics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ButtonAnalyticsRepository extends JpaRepository<ButtonAnalytics, Long> {
    Optional<ButtonAnalytics> findByButtonName(String buttonName);
}

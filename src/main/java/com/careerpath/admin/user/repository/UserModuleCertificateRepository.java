package com.careerpath.admin.user.repository;

import com.careerpath.admin.user.entity.UserModuleCertificate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserModuleCertificateRepository extends JpaRepository<UserModuleCertificate, Long> {

    List<UserModuleCertificate> findByUserId(Long userId);

    Optional<UserModuleCertificate> findByUserIdAndModuleId(Long userId, Long moduleId);

    Optional<UserModuleCertificate> findByCertificateId(String certificateId);

    boolean existsByUserIdAndModuleId(Long userId, Long moduleId);
}

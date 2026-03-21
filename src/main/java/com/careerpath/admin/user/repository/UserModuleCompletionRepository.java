package com.careerpath.admin.user.repository;

import com.careerpath.admin.entity.User;
import com.careerpath.admin.user.entity.UserModuleCompletion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserModuleCompletionRepository extends JpaRepository<UserModuleCompletion, Long> {

    List<UserModuleCompletion> findByUser(User user);

    boolean existsByUserAndModuleId(User user, Long moduleId);
}

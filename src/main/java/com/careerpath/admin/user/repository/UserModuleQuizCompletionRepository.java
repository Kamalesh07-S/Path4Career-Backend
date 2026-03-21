package com.careerpath.admin.user.repository;

import com.careerpath.admin.user.entity.UserModuleQuizCompletion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserModuleQuizCompletionRepository extends JpaRepository<UserModuleQuizCompletion, Long> {

    List<UserModuleQuizCompletion> findByUserIdAndModuleId(Long userId, Long moduleId);

    List<UserModuleQuizCompletion> findByUserId(Long userId);
}

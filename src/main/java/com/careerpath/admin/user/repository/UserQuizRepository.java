package com.careerpath.admin.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.careerpath.admin.user.entity.UserQuiz;
import java.util.List;

public interface UserQuizRepository extends JpaRepository<UserQuiz, Long> {

    List<UserQuiz> findByUserId(Long userId);

    List<UserQuiz> findByUserIdAndQuizId(Long userId, Long quizId);
}

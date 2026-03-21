package com.careerpath.admin.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.careerpath.admin.user.entity.Quiz;
import java.util.List;
import java.util.Optional;

public interface QuizRepository extends JpaRepository<Quiz, Long> {

    List<Quiz> findByActiveTrue();

    Optional<Quiz> findFirstByTutorialId(String tutorialId);
}

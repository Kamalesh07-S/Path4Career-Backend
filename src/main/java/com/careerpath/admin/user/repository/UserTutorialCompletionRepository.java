package com.careerpath.admin.user.repository;

import com.careerpath.admin.entity.User;
import com.careerpath.admin.user.entity.UserTutorialCompletion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserTutorialCompletionRepository extends JpaRepository<UserTutorialCompletion, Long> {

    List<UserTutorialCompletion> findByUser(User user);

    Optional<UserTutorialCompletion> findByUserAndSkillId(User user, Long skillId);

    boolean existsByUserAndSkillId(User user, Long skillId);

    boolean existsByUserAndSkill_TutorialId(User user, String tutorialId);

    List<UserTutorialCompletion> findByUserAndSkillIdIn(User user, List<Long> skillIds);
}

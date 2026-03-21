package com.careerpath.admin.user.repository;

import com.careerpath.admin.entity.User;
import com.careerpath.admin.user.entity.UserSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UserSkillRepository extends JpaRepository<UserSkill, Long> {
    List<UserSkill> findByUser(User user);

    Optional<UserSkill> findByUserAndSkillName(User user, String skillName);
}

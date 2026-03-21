package com.careerpath.admin.user.repository;

import com.careerpath.admin.entity.User;
import com.careerpath.admin.user.entity.UserAchievement;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UserAchievementRepository extends JpaRepository<UserAchievement, Long> {
    List<UserAchievement> findByUser(User user);
}

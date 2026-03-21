package com.careerpath.admin.user.repository;

import com.careerpath.admin.entity.User;
import com.careerpath.admin.user.entity.UserActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UserActivityRepository extends JpaRepository<UserActivity, Long> {
    List<UserActivity> findByUserOrderByCreatedAtDesc(User user);
}

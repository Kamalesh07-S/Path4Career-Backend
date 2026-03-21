package com.careerpath.admin.user.repository;

import com.careerpath.admin.entity.User;
import com.careerpath.admin.user.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    Optional<UserProfile> findByUser(User user);
}

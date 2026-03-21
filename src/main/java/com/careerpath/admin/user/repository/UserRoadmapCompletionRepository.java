package com.careerpath.admin.user.repository;

import com.careerpath.admin.entity.User;
import com.careerpath.admin.user.entity.UserRoadmapCompletion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRoadmapCompletionRepository extends JpaRepository<UserRoadmapCompletion, Long> {

    List<UserRoadmapCompletion> findByUser(User user);

    boolean existsByUserAndRoadmapId(User user, Long roadmapId);

}

package com.careerpath.admin.user.service;

import org.springframework.stereotype.Service;

import com.careerpath.admin.entity.User;
import com.careerpath.admin.user.entity.ActivityType;
import com.careerpath.admin.user.entity.UserActivity;
import com.careerpath.admin.user.repository.UserActivityRepository;

@Service
public class ActivityService {

    private final UserActivityRepository userActivityRepository;

    public ActivityService(UserActivityRepository userActivityRepository) {
        this.userActivityRepository = userActivityRepository;
    }

    public void logActivity(User user, ActivityType type, String description) {
        UserActivity activity = new UserActivity();
        activity.setUser(user);
        activity.setType(type);
        activity.setDescription(description);
        userActivityRepository.save(activity);
    }
}

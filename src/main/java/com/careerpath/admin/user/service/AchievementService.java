package com.careerpath.admin.user.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.careerpath.admin.entity.User;
import com.careerpath.admin.user.entity.UserAchievement;
import com.careerpath.admin.user.repository.UserAchievementRepository;

@Service
public class AchievementService {

    private final UserAchievementRepository userAchievementRepository;

    public AchievementService(UserAchievementRepository userAchievementRepository) {
        this.userAchievementRepository = userAchievementRepository;
    }

    public void evaluateAndGrant(User user, int overallSkillProgress,
            int coursesCompleted, int jobsApplied) {

        Set<String> existingTitles = userAchievementRepository.findByUser(user)
                .stream()
                .map(a -> a.getTitle().toLowerCase())
                .collect(Collectors.toSet());

        if (overallSkillProgress >= 10)
            awardIfMissing(user, existingTitles, "🌱 First Skill Started", "first-skill");
        if (overallSkillProgress >= 40)
            awardIfMissing(user, existingTitles, "🔥 Consistency Starter", "consistency");
        if (overallSkillProgress >= 70)
            awardIfMissing(user, existingTitles, "⭐ Skill Pro", "skill-pro");
        if (overallSkillProgress == 100)
            awardIfMissing(user, existingTitles, "🏆 Skill Master", "skill-master");

        if (coursesCompleted >= 1)
            awardIfMissing(user, existingTitles, "📚 First Course Completed", "first-course");
        if (coursesCompleted >= 5)
            awardIfMissing(user, existingTitles, "📖 Avid Learner", "avid-learner");
        if (coursesCompleted >= 10)
            awardIfMissing(user, existingTitles, "🎓 Course Champion", "course-champion");

        if (jobsApplied >= 1)
            awardIfMissing(user, existingTitles, "💼 First Job Application", "first-job");
        if (jobsApplied >= 5)
            awardIfMissing(user, existingTitles, "🚀 Active Job Seeker", "job-seeker");
        if (jobsApplied >= 10)
            awardIfMissing(user, existingTitles, "🎯 Job Hunter Pro", "job-hunter");
    }

    private void awardIfMissing(User user, Set<String> existingTitles,
            String title, String icon) {
        if (existingTitles.contains(title.toLowerCase()))
            return;

        UserAchievement achievement = new UserAchievement();
        achievement.setUser(user);
        achievement.setTitle(title);
        achievement.setIcon(icon);
        userAchievementRepository.save(achievement);
    }

    public List<UserAchievement> getAchievementsForUser(User user) {
        return userAchievementRepository.findByUser(user);
    }
}

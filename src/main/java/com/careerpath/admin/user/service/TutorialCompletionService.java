package com.careerpath.admin.user.service;

import com.careerpath.admin.entity.Module;
import com.careerpath.admin.entity.Skill;
import com.careerpath.admin.entity.User;
import com.careerpath.admin.repository.ModuleRepo;
import com.careerpath.admin.repository.SkillRepo;
import com.careerpath.admin.repository.UserRepo;
import com.careerpath.admin.user.entity.ActivityType;
import com.careerpath.admin.user.entity.UserModuleCompletion;
import com.careerpath.admin.user.entity.UserRoadmapCompletion;
import com.careerpath.admin.user.entity.UserTutorialCompletion;
import com.careerpath.admin.user.repository.UserModuleCompletionRepository;
import com.careerpath.admin.user.repository.UserRoadmapCompletionRepository;
import com.careerpath.admin.user.repository.UserTutorialCompletionRepository;
import com.careerpath.admin.entity.Roadmap;
import com.careerpath.admin.entity.RoadmapModule;
import com.careerpath.admin.repository.RoadmapModuleRepo;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TutorialCompletionService {

    private final UserTutorialCompletionRepository tutorialCompletionRepo;
    private final UserModuleCompletionRepository moduleCompletionRepo;
    private final UserRoadmapCompletionRepository userRoadmapCompletionRepo;
    private final RoadmapModuleRepo roadmapModuleRepo;
    private final UserRepo userRepo;
    private final SkillRepo skillRepo;
    private final ModuleRepo moduleRepo;
    private final ActivityService activityService;

    public TutorialCompletionService(UserTutorialCompletionRepository tutorialCompletionRepo,
            UserModuleCompletionRepository moduleCompletionRepo,
            UserRoadmapCompletionRepository userRoadmapCompletionRepo,
            RoadmapModuleRepo roadmapModuleRepo,
            UserRepo userRepo,
            SkillRepo skillRepo,
            ModuleRepo moduleRepo,
            ActivityService activityService) {
        this.tutorialCompletionRepo = tutorialCompletionRepo;
        this.moduleCompletionRepo = moduleCompletionRepo;
        this.userRoadmapCompletionRepo = userRoadmapCompletionRepo;
        this.roadmapModuleRepo = roadmapModuleRepo;
        this.userRepo = userRepo;
        this.skillRepo = skillRepo;
        this.moduleRepo = moduleRepo;
        this.activityService = activityService;
    }

    private User resolveUser(Authentication authentication) {
        User user = userRepo.findByEmail(authentication.getName());
        if (user == null)
            throw new RuntimeException("User not found");
        return user;
    }

    /**
     * Mark a skill's tutorial as completed for the authenticated user.
     * Also checks if the parent module is now fully completed.
     */
    public void markCompleted(Authentication authentication, Long skillId) {
        User user = resolveUser(authentication);

        // Already completed — just return
        if (tutorialCompletionRepo.existsByUserAndSkillId(user, skillId)) {
            return;
        }

        Skill skill = skillRepo.findById(skillId)
                .orElseThrow(() -> new RuntimeException("Skill not found: " + skillId));

        UserTutorialCompletion completion = new UserTutorialCompletion();
        completion.setUser(user);
        completion.setSkill(skill);
        tutorialCompletionRepo.save(completion);

        activityService.logActivity(user, ActivityType.COURSE_COMPLETE,
                "Completed tutorial: " + skill.getName());

        checkAndMarkModuleCompletions(user, skillId);
        checkAndMarkRoadmapCompletions(user, skillId);
    }

    /**
     * Checks all modules that contain this skill. If all active skills in a module
     * are now completed by the user, mark the entire module as completed.
     */
    private void checkAndMarkModuleCompletions(User user, Long newlyCompletedSkillId) {
        List<Module> modules = moduleRepo.findBySkillsId(newlyCompletedSkillId);

        for (Module module : modules) {
            // Only care if module is active
            if (!module.isActive())
                continue;

            // Check if user already completed this module
            if (moduleCompletionRepo.existsByUserAndModuleId(user, module.getId())) {
                continue;
            }

            // Get all active skills in this module
            List<Skill> activeSkills = module.getSkills().stream()
                    .filter(Skill::isActive)
                    .collect(Collectors.toList());

            if (activeSkills.isEmpty())
                continue;

            // Get all completed skills for this user
            List<Long> userCompletedSkillIds = tutorialCompletionRepo.findByUser(user)
                    .stream()
                    .map(c -> c.getSkill().getId())
                    .collect(Collectors.toList());

            // Check if user has completed ALL active skills for this module
            boolean allCompleted = activeSkills.stream()
                    .allMatch(s -> userCompletedSkillIds.contains(s.getId()));

            if (allCompleted) {
                // Mark module as completed
                UserModuleCompletion moduleCompletion = new UserModuleCompletion();
                moduleCompletion.setUser(user);
                moduleCompletion.setModule(module);
                moduleCompletionRepo.save(moduleCompletion);

                activityService.logActivity(user, ActivityType.COURSE_COMPLETE,
                        "Completed entirely module: " + module.getName());
            }
        }
    }

    /**
     * Checks all roadmaps that contain a module which contains this skill.
     * If all active skills in all active modules in an active roadmap
     * are now completed by the user, mark the entire roadmap as completed.
     */
    private void checkAndMarkRoadmapCompletions(User user, Long newlyCompletedSkillId) {
        List<Module> modules = moduleRepo.findBySkillsId(newlyCompletedSkillId);
        if (modules.isEmpty())
            return;

        List<RoadmapModule> allRoadmapModules = new java.util.ArrayList<>();
        for (Module m : modules) {
            allRoadmapModules.addAll(roadmapModuleRepo.findByModuleId(m.getId()));
        }

        List<Roadmap> roadmapsToCheck = allRoadmapModules.stream()
                .map(RoadmapModule::getRoadmap)
                .distinct()
                .filter(r -> r.isActive() && r.isPublished())
                .collect(Collectors.toList());

        if (roadmapsToCheck.isEmpty())
            return;

        List<Long> userCompletedSkillIds = tutorialCompletionRepo.findByUser(user)
                .stream()
                .map(c -> c.getSkill().getId())
                .collect(Collectors.toList());

        for (Roadmap roadmap : roadmapsToCheck) {
            if (userRoadmapCompletionRepo.existsByUserAndRoadmapId(user, roadmap.getId())) {
                continue;
            }

            List<Skill> allActiveSkills = roadmap.getModules().stream()
                    .map(RoadmapModule::getModule)
                    .filter(Module::isActive)
                    .flatMap(m -> m.getSkills().stream())
                    .filter(Skill::isActive)
                    .collect(Collectors.toList());

            if (allActiveSkills.isEmpty())
                continue;

            boolean allCompleted = allActiveSkills.stream()
                    .allMatch(s -> userCompletedSkillIds.contains(s.getId()));

            if (allCompleted) {
                UserRoadmapCompletion roadmapCompletion = new UserRoadmapCompletion();
                roadmapCompletion.setUser(user);
                roadmapCompletion.setRoadmap(roadmap);
                userRoadmapCompletionRepo.save(roadmapCompletion);

                activityService.logActivity(user, ActivityType.COURSE_COMPLETE,
                        "Completed entirely roadmap: " + roadmap.getTitle());
            }
        }
    }

    /**
     * Get all completed skill IDs for the authenticated user.
     */
    public List<Long> getCompletedSkillIds(Authentication authentication) {
        User user = resolveUser(authentication);
        return tutorialCompletionRepo.findByUser(user).stream()
                .map(c -> c.getSkill().getId())
                .collect(Collectors.toList());
    }

    /**
     * Check if a specific skill's tutorial is completed.
     */
    public boolean isSkillCompleted(Authentication authentication, Long skillId) {
        User user = resolveUser(authentication);
        return tutorialCompletionRepo.existsByUserAndSkillId(user, skillId);
    }

    /**
     * Check if a specific skill's tutorial is completed.
     */
    public boolean isTutorialCompleted(Authentication authentication, String tutorialId) {
        User user = resolveUser(authentication);
        return tutorialCompletionRepo.existsByUserAndSkill_TutorialId(user, tutorialId);
    }

    /**
     * Mark a tutorial as completed using the string tutorial_id.
     * Looks up the Skill by tutorialId, then delegates to markCompleted.
     */
    public void markCompletedByTutorialId(Authentication authentication, String tutorialId) {
        Skill skill = skillRepo.findFirstByTutorialId(tutorialId)
                .orElseThrow(() -> new RuntimeException("No skill found with tutorialId: " + tutorialId));
        markCompleted(authentication, skill.getId());
    }

    /**
     * Get skill ID by string tutorial_id.
     */
    public Long getSkillIdByTutorialId(String tutorialId) {
        return skillRepo.findFirstByTutorialId(tutorialId)
                .map(Skill::getId)
                .orElse(null);
    }
}

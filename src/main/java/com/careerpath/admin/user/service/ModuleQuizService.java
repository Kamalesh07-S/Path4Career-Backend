package com.careerpath.admin.user.service;

import com.careerpath.admin.entity.Module;
import com.careerpath.admin.entity.Skill;
import com.careerpath.admin.entity.User;
import com.careerpath.admin.repository.ModuleRepo;
import com.careerpath.admin.repository.UserRepo;
import com.careerpath.admin.user.dto.ModuleQuizResultDTO;
import com.careerpath.admin.user.dto.ModuleQuizSubmitRequest;
import com.careerpath.admin.user.entity.UserModuleQuizCompletion;
import com.careerpath.admin.user.repository.UserModuleQuizCompletionRepository;
import com.careerpath.admin.user.repository.UserTutorialCompletionRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ModuleQuizService {

    private static final Logger log = LoggerFactory.getLogger(ModuleQuizService.class);

    private final ModuleRepo moduleRepo;
    private final UserRepo userRepo;
    private final UserTutorialCompletionRepository tutorialCompletionRepo;
    private final UserModuleQuizCompletionRepository moduleQuizCompletionRepo;
    private final CertificateService certificateService;

    public ModuleQuizService(ModuleRepo moduleRepo,
                             UserRepo userRepo,
                             UserTutorialCompletionRepository tutorialCompletionRepo,
                             UserModuleQuizCompletionRepository moduleQuizCompletionRepo,
                             CertificateService certificateService) {
        this.moduleRepo = moduleRepo;
        this.userRepo = userRepo;
        this.tutorialCompletionRepo = tutorialCompletionRepo;
        this.moduleQuizCompletionRepo = moduleQuizCompletionRepo;
        this.certificateService = certificateService;
    }

    private User resolveUser(Authentication authentication) {
        User user = userRepo.findByEmail(authentication.getName());
        if (user == null) throw new RuntimeException("User not found");
        return user;
    }

    /**
     * Check if the user is eligible to take the module quiz.
     * Returns a map with eligibility info and the list of skill tutorialIds for question loading.
     */
    public Map<String, Object> checkEligibility(Authentication authentication, Long moduleId) {
        User user = resolveUser(authentication);

        Module module = moduleRepo.findById(moduleId)
                .orElseThrow(() -> new RuntimeException("Module not found: " + moduleId));

        List<Skill> activeSkills = module.getSkills().stream()
                .filter(Skill::isActive)
                .collect(Collectors.toList());

        int totalSkills = activeSkills.size();

        // Get user's completed skill IDs
        List<Long> completedSkillIds = tutorialCompletionRepo.findByUser(user).stream()
                .map(c -> c.getSkill().getId())
                .collect(Collectors.toList());

        // Count how many of this module's skills are completed
        long completedCount = activeSkills.stream()
                .filter(s -> completedSkillIds.contains(s.getId()))
                .count();

        boolean eligible = completedCount >= totalSkills && totalSkills > 0;

        // Collect tutorialIds for each skill (used by frontend to load quiz JSONs)
        List<String> skillTutorialIds = activeSkills.stream()
                .map(Skill::getTutorialId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        // Check for previous quiz attempts
        List<UserModuleQuizCompletion> previousAttempts =
                moduleQuizCompletionRepo.findByUserIdAndModuleId(user.getId(), moduleId);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("eligible", eligible);
        result.put("completedSkills", (int) completedCount);
        result.put("totalSkills", totalSkills);
        result.put("moduleName", module.getName());
        result.put("skillTutorialIds", skillTutorialIds);
        result.put("previousAttempts", previousAttempts.size());

        if (!previousAttempts.isEmpty()) {
            // Get the best score
            previousAttempts.sort((a, b) -> Integer.compare(b.getPercentage(), a.getPercentage()));
            result.put("bestScore", previousAttempts.get(0).getPercentage());
            result.put("bestPassed", previousAttempts.get(0).isPassed());
        }

        return result;
    }

    /**
     * Submit a module quiz result.
     */
    public ModuleQuizResultDTO submitQuizResult(Authentication authentication, Long moduleId,
                                                 ModuleQuizSubmitRequest request) {
        User user = resolveUser(authentication);

        Module module = moduleRepo.findById(moduleId)
                .orElseThrow(() -> new RuntimeException("Module not found: " + moduleId));

        UserModuleQuizCompletion completion = new UserModuleQuizCompletion();
        completion.setUserId(user.getId());
        completion.setModuleId(moduleId);
        completion.setScore(request.getScore());
        completion.setTotalQuestions(request.getTotalQuestions());
        completion.setPercentage(request.getPercentage());
        completion.setPassed(request.isPassed());
        completion.setTimeTakenSeconds(request.getTimeTakenSeconds());

        UserModuleQuizCompletion saved = moduleQuizCompletionRepo.save(completion);

        // Auto-issue certificate if the quiz was passed
        if (saved.isPassed()) {
            try {
                certificateService.issueCertificate(
                        user.getId(), moduleId, saved.getId(), saved.getPercentage());
                log.info("Certificate issued for user {} on module {}", user.getId(), moduleId);
            } catch (Exception e) {
                log.warn("Failed to auto-issue certificate for user {} module {}: {}",
                        user.getId(), moduleId, e.getMessage());
            }
        }

        return new ModuleQuizResultDTO(
                saved.getId(),
                saved.getModuleId(),
                module.getName(),
                saved.getScore(),
                saved.getTotalQuestions(),
                saved.getPercentage(),
                saved.isPassed(),
                saved.getTimeTakenSeconds(),
                saved.getAttemptedAt()
        );
    }

    /**
     * Get quiz history for a specific module.
     */
    public List<ModuleQuizResultDTO> getQuizHistory(Authentication authentication, Long moduleId) {
        User user = resolveUser(authentication);

        Module module = moduleRepo.findById(moduleId)
                .orElseThrow(() -> new RuntimeException("Module not found: " + moduleId));

        return moduleQuizCompletionRepo.findByUserIdAndModuleId(user.getId(), moduleId)
                .stream()
                .map(c -> new ModuleQuizResultDTO(
                        c.getId(),
                        c.getModuleId(),
                        module.getName(),
                        c.getScore(),
                        c.getTotalQuestions(),
                        c.getPercentage(),
                        c.isPassed(),
                        c.getTimeTakenSeconds(),
                        c.getAttemptedAt()
                ))
                .collect(Collectors.toList());
    }
}

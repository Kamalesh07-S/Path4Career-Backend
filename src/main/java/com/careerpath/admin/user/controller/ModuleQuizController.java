package com.careerpath.admin.user.controller;

import com.careerpath.admin.user.dto.ModuleQuizResultDTO;
import com.careerpath.admin.user.dto.ModuleQuizSubmitRequest;
import com.careerpath.admin.user.service.ModuleQuizService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/modules")
public class ModuleQuizController {

    private final ModuleQuizService moduleQuizService;

    public ModuleQuizController(ModuleQuizService moduleQuizService) {
        this.moduleQuizService = moduleQuizService;
    }

    /**
     * GET /api/v1/modules/{moduleId}/quiz-eligibility
     * Check if user has completed all skills in the module and is eligible to take the quiz.
     * Returns eligibility status, skill tutorialIds (for loading quiz JSONs), and previous attempts.
     */
    @GetMapping("/{moduleId}/quiz-eligibility")
    public ResponseEntity<?> checkQuizEligibility(Authentication authentication,
                                                   @PathVariable Long moduleId) {
        try {
            Map<String, Object> eligibility = moduleQuizService.checkEligibility(authentication, moduleId);
            return ResponseEntity.ok(eligibility);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * POST /api/v1/modules/{moduleId}/quiz/submit
     * Submit a module quiz result. Saves to user_module_quiz_completions table.
     */
    @PostMapping("/{moduleId}/quiz/submit")
    public ResponseEntity<?> submitModuleQuiz(Authentication authentication,
                                               @PathVariable Long moduleId,
                                               @RequestBody ModuleQuizSubmitRequest request) {
        try {
            ModuleQuizResultDTO result = moduleQuizService.submitQuizResult(authentication, moduleId, request);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /api/v1/modules/{moduleId}/quiz-history
     * Get user's quiz attempt history for a specific module.
     */
    @GetMapping("/{moduleId}/quiz-history")
    public ResponseEntity<?> getQuizHistory(Authentication authentication,
                                             @PathVariable Long moduleId) {
        try {
            List<ModuleQuizResultDTO> history = moduleQuizService.getQuizHistory(authentication, moduleId);
            return ResponseEntity.ok(history);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
}

package com.careerpath.admin.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.careerpath.admin.user.dto.QuizDTO;
import com.careerpath.admin.user.dto.QuizSubmitRequest;
import com.careerpath.admin.user.dto.UserQuizDTO;
import com.careerpath.admin.user.service.QuizService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/quiz")
public class QuizController {

    private final QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    /**
     * GET /api/quiz/list — List all active quizzes (public)
     */
    @GetMapping("/list")
    public List<QuizDTO> listQuizzes() {
        return quizService.getAllActiveQuizzes();
    }

    /**
     * GET /api/quiz/{tutorialId} — Get quiz details for a tutorial (public)
     */
    @GetMapping("/{tutorialId}")
    public ResponseEntity<QuizDTO> getQuizByTutorial(@PathVariable String tutorialId) {
        try {
            QuizDTO quiz = quizService.getQuizByTutorialId(tutorialId);
            return ResponseEntity.ok(quiz);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * POST /api/quiz/submit — Submit quiz result (JWT auth required)
     * User identified from JWT token via Spring Security Authentication.
     */
    @PostMapping("/submit")
    public ResponseEntity<?> submitQuiz(Authentication authentication,
                                        @RequestBody QuizSubmitRequest request) {
        try {
            UserQuizDTO result = quizService.submitQuizResult(authentication, request);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /api/quiz/history — Get user's quiz attempts (JWT auth required)
     * User identified from JWT token via Spring Security Authentication.
     */
    @GetMapping("/history")
    public ResponseEntity<?> getQuizHistory(Authentication authentication) {
        try {
            List<UserQuizDTO> history = quizService.getUserQuizHistory(authentication);
            return ResponseEntity.ok(history);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
}

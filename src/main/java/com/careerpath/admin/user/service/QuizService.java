package com.careerpath.admin.user.service;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.careerpath.admin.entity.User;
import com.careerpath.admin.repository.UserRepo;
import com.careerpath.admin.user.dto.QuizDTO;
import com.careerpath.admin.user.dto.QuizSubmitRequest;
import com.careerpath.admin.user.dto.UserQuizDTO;
import com.careerpath.admin.user.entity.Quiz;
import com.careerpath.admin.user.entity.UserQuiz;
import com.careerpath.admin.user.repository.QuizRepository;
import com.careerpath.admin.user.repository.UserQuizRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuizService {

    private final QuizRepository quizRepository;
    private final UserQuizRepository userQuizRepository;
    private final UserRepo userRepo;

    public QuizService(QuizRepository quizRepository,
                       UserQuizRepository userQuizRepository,
                       UserRepo userRepo) {
        this.quizRepository = quizRepository;
        this.userQuizRepository = userQuizRepository;
        this.userRepo = userRepo;
    }

    /**
     * Get all active quizzes as DTOs.
     */
    public List<QuizDTO> getAllActiveQuizzes() {
        return quizRepository.findByActiveTrue()
                .stream()
                .map(this::toQuizDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get quiz by tutorial ID.
     */
    public QuizDTO getQuizByTutorialId(String tutorialId) {
        Quiz quiz = quizRepository.findFirstByTutorialId(tutorialId)
                .orElseThrow(() -> new RuntimeException("Quiz not found for tutorial: " + tutorialId));
        return toQuizDTO(quiz);
    }

    /**
     * Submit quiz result — save user's attempt.
     * Uses Spring Security Authentication to identify the user.
     */
    public UserQuizDTO submitQuizResult(Authentication authentication, QuizSubmitRequest request) {
        String email = authentication.getName();
        User user = userRepo.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("User not found: " + email);
        }

        // Resolve quizId — either from request or by tutorialId
        Long quizId = request.getQuizId();
        Quiz quiz;
        if (quizId != null) {
            quiz = quizRepository.findById(quizId)
                    .orElseThrow(() -> new RuntimeException("Quiz not found: " + quizId));
        } else if (request.getTutorialId() != null) {
            quiz = quizRepository.findFirstByTutorialId(request.getTutorialId())
                    .orElseThrow(() -> new RuntimeException("Quiz not found for tutorial: " + request.getTutorialId()));
        } else {
            throw new RuntimeException("Either quizId or tutorialId must be provided");
        }

        // Create UserQuiz record
        UserQuiz userQuiz = new UserQuiz();
        userQuiz.setUserId(user.getId());
        userQuiz.setQuizId(quiz.getId());
        userQuiz.setScore(request.getScore());
        userQuiz.setTotalQuestions(request.getTotalQuestions());
        userQuiz.setPercentage(request.getPercentage());
        userQuiz.setPassed(request.isPassed());
        userQuiz.setTimeTakenSeconds(request.getTimeTakenSeconds());

        UserQuiz saved = userQuizRepository.save(userQuiz);

        return toUserQuizDTO(saved, quiz);
    }

    /**
     * Get user's quiz history.
     * Uses Spring Security Authentication to identify the user.
     */
    public List<UserQuizDTO> getUserQuizHistory(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepo.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("User not found: " + email);
        }

        return userQuizRepository.findByUserId(user.getId())
                .stream()
                .map(uq -> {
                    Quiz quiz = quizRepository.findById(uq.getQuizId()).orElse(null);
                    return toUserQuizDTO(uq, quiz);
                })
                .collect(Collectors.toList());
    }

    // ── Mapping helpers ──

    private QuizDTO toQuizDTO(Quiz quiz) {
        return new QuizDTO(
                quiz.getId(),
                quiz.getTitle(),
                quiz.getTutorialId(),
                quiz.getDescription(),
                quiz.getTotalQuestions(),
                quiz.getTimeLimitMinutes(),
                quiz.getPassingPercentage()
        );
    }

    private UserQuizDTO toUserQuizDTO(UserQuiz uq, Quiz quiz) {
        return new UserQuizDTO(
                uq.getId(),
                uq.getQuizId(),
                quiz != null ? quiz.getTitle() : "Unknown Quiz",
                quiz != null ? quiz.getTutorialId() : "",
                uq.getScore(),
                uq.getTotalQuestions(),
                uq.getPercentage(),
                uq.isPassed(),
                uq.getTimeTakenSeconds(),
                uq.getAttemptedAt()
        );
    }
}

package com.careerpath.admin.user.service;

import com.careerpath.admin.entity.Module;
import com.careerpath.admin.entity.Skill;
import com.careerpath.admin.entity.User;
import com.careerpath.admin.repository.ModuleRepo;
import com.careerpath.admin.repository.UserRepo;
import com.careerpath.admin.user.dto.CertificateResponseDTO;
import com.careerpath.admin.user.entity.UserModuleCertificate;
import com.careerpath.admin.user.entity.UserModuleQuizCompletion;
import com.careerpath.admin.user.repository.UserModuleCertificateRepository;
import com.careerpath.admin.user.repository.UserModuleQuizCompletionRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Year;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
public class CertificateService {

    private static final Logger log = LoggerFactory.getLogger(CertificateService.class);

    private final UserModuleCertificateRepository certificateRepo;
    private final UserModuleQuizCompletionRepository quizCompletionRepo;
    private final ModuleRepo moduleRepo;
    private final UserRepo userRepo;

    public CertificateService(UserModuleCertificateRepository certificateRepo,
                               UserModuleQuizCompletionRepository quizCompletionRepo,
                               ModuleRepo moduleRepo,
                               UserRepo userRepo) {
        this.certificateRepo = certificateRepo;
        this.quizCompletionRepo = quizCompletionRepo;
        this.moduleRepo = moduleRepo;
        this.userRepo = userRepo;
    }

    private User resolveUser(Authentication authentication) {
        User user = userRepo.findByEmail(authentication.getName());
        if (user == null) throw new RuntimeException("User not found");
        return user;
    }

    /**
     * Issue a certificate for a user who passed a module quiz.
     * Idempotent — will not create a duplicate if one already exists.
     * Returns the certificate entity (existing or newly created).
     */
    public UserModuleCertificate issueCertificate(Long userId, Long moduleId,
                                                   Long quizCompletionId, int scorePercentage) {
        // Check if certificate already exists
        Optional<UserModuleCertificate> existing = certificateRepo.findByUserIdAndModuleId(userId, moduleId);
        if (existing.isPresent()) {
            return existing.get(); // Already issued — return existing
        }

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        // Generate certificate ID BEFORE saving (certificate_id is NOT NULL)
        String certId = generateCertificateId();

        // Create new certificate with all required fields set
        UserModuleCertificate cert = new UserModuleCertificate();
        cert.setUserId(userId);
        cert.setModuleId(moduleId);
        cert.setCertificateId(certId);
        cert.setQuizCompletionId(quizCompletionId);
        cert.setScorePercentage(scorePercentage);
        cert.setIssuedToName(user.getUsername());
        cert.setIssuedToEmail(user.getEmail());

        UserModuleCertificate saved = certificateRepo.save(cert);
        log.info("Certificate {} issued for user {} on module {}", certId, userId, moduleId);
        return saved;
    }

    /**
     * Generate a unique certificate ID: CP-YYYY-RRRRR
     * where RRRRR is a random 5-digit number.
     * If collision, retry with a new random.
     */
    private String generateCertificateId() {
        int year = Year.now().getValue();
        for (int attempt = 0; attempt < 10; attempt++) {
            int random = ThreadLocalRandom.current().nextInt(10000, 99999);
            String certId = String.format("CP-%d-%05d", year, random);
            if (certificateRepo.findByCertificateId(certId).isEmpty()) {
                return certId;
            }
        }
        // Fallback: use timestamp-based ID
        return String.format("CP-%d-%d", year, System.currentTimeMillis() % 100000);
    }

    /**
     * Get all certificates for the currently logged-in user.
     * Also retroactively issues certificates for any passed quiz completions
     * that don't have a certificate record yet (handles pre-existing data).
     */
    public List<CertificateResponseDTO> getUserCertificates(Authentication authentication) {
        User user = resolveUser(authentication);

        // Retroactively issue certificates for passed quizzes without a certificate
        retroactivelyIssueCertificates(user.getId());

        List<UserModuleCertificate> certificates = certificateRepo.findByUserId(user.getId());

        return certificates.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Explicitly refresh certificates by scanning quiz completions
     * and issuing any missing certificates. Returns the full updated list.
     * Called by POST /api/v1/certificates/refresh
     */
    public List<CertificateResponseDTO> refreshAndGetCertificates(Authentication authentication) {
        User user = resolveUser(authentication);
        log.info("Certificate refresh requested for user {} ({})", user.getId(), user.getEmail());

        // Get ALL quiz completions for this user
        List<UserModuleQuizCompletion> allCompletions = quizCompletionRepo.findByUserId(user.getId());
        log.info("Found {} total quiz completions for user {}", allCompletions.size(), user.getId());

        // Group by moduleId, find the best passed attempt per module
        Map<Long, UserModuleQuizCompletion> bestPassedByModule = new HashMap<>();
        for (UserModuleQuizCompletion c : allCompletions) {
            log.info("  Quiz completion: moduleId={}, score={}%, passed={}", 
                    c.getModuleId(), c.getPercentage(), c.isPassed());
            if (!c.isPassed()) continue;
            Long mid = c.getModuleId();
            if (!bestPassedByModule.containsKey(mid) ||
                    c.getPercentage() > bestPassedByModule.get(mid).getPercentage()) {
                bestPassedByModule.put(mid, c);
            }
        }

        log.info("Found {} modules with passed quizzes", bestPassedByModule.size());

        // Issue certificates for any module that has a passed quiz but no certificate
        for (Map.Entry<Long, UserModuleQuizCompletion> entry : bestPassedByModule.entrySet()) {
            Long moduleId = entry.getKey();
            boolean exists = certificateRepo.existsByUserIdAndModuleId(user.getId(), moduleId);
            log.info("  Module {}: certificate exists={}", moduleId, exists);

            if (!exists) {
                UserModuleQuizCompletion best = entry.getValue();
                try {
                    UserModuleCertificate issued = issueCertificate(
                            user.getId(), moduleId, best.getId(), best.getPercentage());
                    log.info("  → Issued certificate {} for module {}", 
                            issued.getCertificateId(), moduleId);
                } catch (Exception e) {
                    log.error("  → FAILED to issue certificate for module {}: {}", 
                            moduleId, e.getMessage(), e);
                }
            }
        }

        // Return the full updated list
        List<UserModuleCertificate> certificates = certificateRepo.findByUserId(user.getId());
        log.info("Returning {} certificates for user {}", certificates.size(), user.getId());

        return certificates.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get a specific certificate for a module.
     * Will retroactively issue if the user passed the quiz but has no certificate.
     */
    public Optional<CertificateResponseDTO> getCertificateForModule(Authentication authentication, Long moduleId) {
        User user = resolveUser(authentication);

        // Check if certificate exists; if not, try retroactive issuance
        if (!certificateRepo.existsByUserIdAndModuleId(user.getId(), moduleId)) {
            retroactivelyIssueForModule(user.getId(), moduleId);
        }

        return certificateRepo.findByUserIdAndModuleId(user.getId(), moduleId)
                .map(this::toDTO);
    }

    /**
     * Public certificate verification — no auth required.
     * Returns certificate data if found and valid.
     */
    public Optional<CertificateResponseDTO> verifyCertificate(String certificateId) {
        return certificateRepo.findByCertificateId(certificateId)
                .map(this::toDTO);
    }

    /**
     * Retroactively issue certificates for all passed module quizzes
     * that don't have a certificate yet. This handles users who passed
     * quizzes before the certificate system was deployed.
     */
    private void retroactivelyIssueCertificates(Long userId) {
        List<UserModuleQuizCompletion> allCompletions = quizCompletionRepo.findByUserId(userId);

        // Group by moduleId, find the best passed attempt per module
        Map<Long, UserModuleQuizCompletion> bestPassedByModule = new HashMap<>();
        for (UserModuleQuizCompletion c : allCompletions) {
            if (!c.isPassed()) continue;
            Long mid = c.getModuleId();
            if (!bestPassedByModule.containsKey(mid) ||
                    c.getPercentage() > bestPassedByModule.get(mid).getPercentage()) {
                bestPassedByModule.put(mid, c);
            }
        }

        // Issue certificates for any module that has a passed quiz but no certificate
        for (Map.Entry<Long, UserModuleQuizCompletion> entry : bestPassedByModule.entrySet()) {
            Long moduleId = entry.getKey();
            if (!certificateRepo.existsByUserIdAndModuleId(userId, moduleId)) {
                UserModuleQuizCompletion best = entry.getValue();
                try {
                    issueCertificate(userId, moduleId, best.getId(), best.getPercentage());
                } catch (Exception e) {
                    log.warn("Retroactive certificate issuance failed for user {} module {}: {}",
                            userId, moduleId, e.getMessage());
                }
            }
        }
    }

    /**
     * Retroactively issue a certificate for a single module if user has passed it.
     */
    private void retroactivelyIssueForModule(Long userId, Long moduleId) {
        List<UserModuleQuizCompletion> completions = quizCompletionRepo.findByUserIdAndModuleId(userId, moduleId);

        completions.stream()
                .filter(UserModuleQuizCompletion::isPassed)
                .max(Comparator.comparingInt(UserModuleQuizCompletion::getPercentage))
                .ifPresent(best -> {
                    try {
                        issueCertificate(userId, moduleId, best.getId(), best.getPercentage());
                    } catch (Exception e) {
                        log.warn("Retroactive certificate issuance failed for user {} module {}: {}",
                                userId, moduleId, e.getMessage());
                    }
                });
    }

    /**
     * Convert entity to DTO, enriching with module data.
     */
    private CertificateResponseDTO toDTO(UserModuleCertificate cert) {
        Module module = moduleRepo.findById(cert.getModuleId()).orElse(null);

        CertificateResponseDTO dto = new CertificateResponseDTO();
        dto.setCertificateId(cert.getCertificateId());
        dto.setModuleId(cert.getModuleId());
        dto.setScorePercentage(cert.getScorePercentage());
        dto.setIssuedToName(cert.getIssuedToName());
        dto.setIssuedAt(cert.getIssuedAt());

        if (module != null) {
            dto.setModuleName(module.getName());
            dto.setModuleDescription(module.getDescription());
            dto.setDifficulty(module.getDifficulty() != null ? module.getDifficulty().name() : null);
            dto.setDurationValue(module.getDurationValue());
            dto.setDurationUnit(module.getDurationUnit() != null ? module.getDurationUnit().name() : null);

            int activeSkillCount = (int) module.getSkills().stream()
                    .filter(Skill::isActive)
                    .count();
            dto.setSkillCount(activeSkillCount);
        }

        return dto;
    }
}

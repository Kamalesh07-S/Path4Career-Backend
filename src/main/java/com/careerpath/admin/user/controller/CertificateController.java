package com.careerpath.admin.user.controller;

import com.careerpath.admin.user.dto.CertificateResponseDTO;
import com.careerpath.admin.user.service.CertificateService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/certificates")
public class CertificateController {

    private final CertificateService certificateService;

    public CertificateController(CertificateService certificateService) {
        this.certificateService = certificateService;
    }

    /**
     * GET /api/v1/certificates
     * Returns all certificates earned by the logged-in user.
     */
    @GetMapping
    public ResponseEntity<?> getUserCertificates(Authentication authentication) {
        try {
            List<CertificateResponseDTO> certificates = certificateService.getUserCertificates(authentication);
            return ResponseEntity.ok(certificates);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /api/v1/certificates/module/{moduleId}
     * Returns the certificate for a specific module if the user has earned one.
     */
    @GetMapping("/module/{moduleId}")
    public ResponseEntity<?> getCertificateForModule(Authentication authentication,
                                                      @PathVariable Long moduleId) {
        try {
            Optional<CertificateResponseDTO> cert = certificateService.getCertificateForModule(authentication, moduleId);
            if (cert.isPresent()) {
                return ResponseEntity.ok(cert.get());
            }
            return ResponseEntity.status(404)
                    .body(Map.of("error", "No certificate found for this module"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /api/v1/certificates/verify/{certificateId}
     * Public endpoint — verifies a certificate by its unique ID (for QR code scanning).
     */
    @GetMapping("/verify/{certificateId}")
    public ResponseEntity<?> verifyCertificate(@PathVariable String certificateId) {
        Optional<CertificateResponseDTO> cert = certificateService.verifyCertificate(certificateId);
        if (cert.isPresent()) {
            return ResponseEntity.ok(cert.get());
        }
        return ResponseEntity.status(404)
                .body(Map.of("error", "Certificate not found or invalid",
                             "certificateId", certificateId));
    }

    /**
     * POST /api/v1/certificates/refresh
     * Scans user_module_quiz_completions for passed quizzes and issues
     * certificates for any that are missing in user_module_certificates.
     * Returns the full updated list of certificates.
     */
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshCertificates(Authentication authentication) {
        try {
            List<CertificateResponseDTO> certificates = certificateService.refreshAndGetCertificates(authentication);
            return ResponseEntity.ok(certificates);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
}

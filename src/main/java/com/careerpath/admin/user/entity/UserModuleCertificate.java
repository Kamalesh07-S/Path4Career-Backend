package com.careerpath.admin.user.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_module_certificates",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "module_id"}))
public class UserModuleCertificate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "module_id", nullable = false)
    private Long moduleId;

    @Column(name = "certificate_id", nullable = false, unique = true, length = 50)
    private String certificateId;

    @Column(name = "quiz_completion_id")
    private Long quizCompletionId;

    @Column(name = "score_percentage", nullable = false)
    private int scorePercentage;

    @Column(name = "issued_at", nullable = false, updatable = false)
    private LocalDateTime issuedAt;

    @Column(name = "issued_to_name", nullable = false)
    private String issuedToName;

    @Column(name = "issued_to_email", nullable = false)
    private String issuedToEmail;

    @PrePersist
    public void onCreate() {
        this.issuedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getModuleId() { return moduleId; }
    public void setModuleId(Long moduleId) { this.moduleId = moduleId; }

    public String getCertificateId() { return certificateId; }
    public void setCertificateId(String certificateId) { this.certificateId = certificateId; }

    public Long getQuizCompletionId() { return quizCompletionId; }
    public void setQuizCompletionId(Long quizCompletionId) { this.quizCompletionId = quizCompletionId; }

    public int getScorePercentage() { return scorePercentage; }
    public void setScorePercentage(int scorePercentage) { this.scorePercentage = scorePercentage; }

    public LocalDateTime getIssuedAt() { return issuedAt; }

    public String getIssuedToName() { return issuedToName; }
    public void setIssuedToName(String issuedToName) { this.issuedToName = issuedToName; }

    public String getIssuedToEmail() { return issuedToEmail; }
    public void setIssuedToEmail(String issuedToEmail) { this.issuedToEmail = issuedToEmail; }
}

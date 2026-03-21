package com.careerpath.admin.user.entity;

import com.careerpath.admin.entity.User;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "user_preferences")
public class UserPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    private boolean emailNotifications;
    private boolean platformNotifications;
    private boolean jobAlerts;
    private String theme;
    private String learningPath;

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isEmailNotifications() {
        return emailNotifications;
    }

    public void setEmailNotifications(boolean v) {
        this.emailNotifications = v;
    }

    public boolean isPlatformNotifications() {
        return platformNotifications;
    }

    public void setPlatformNotifications(boolean v) {
        this.platformNotifications = v;
    }

    public boolean isJobAlerts() {
        return jobAlerts;
    }

    public void setJobAlerts(boolean v) {
        this.jobAlerts = v;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getLearningPath() {
        return learningPath;
    }

    public void setLearningPath(String learningPath) {
        this.learningPath = learningPath;
    }
}

package com.careerpath.admin.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import com.careerpath.admin.entity.Module;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "skills")

public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String category;

    private String level;

    private String description;

    @Column(name = "tutorial_id", length = 100)
    private String tutorialId;

    private boolean active = true;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public LocalDateTime getLastModifiedAt() {
        return lastModifiedAt;
    }

    @Column(nullable = false)
    private LocalDateTime lastModifiedAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.lastModifiedAt = this.createdAt;
    }

    @PreUpdate
    public void onUpdate() {
        this.lastModifiedAt = LocalDateTime.now();
    }

    @ManyToMany(mappedBy = "skills")
    private List<Module> modules = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTutorialId() {
        return tutorialId;
    }

    public void setTutorialId(String tutorialId) {
        this.tutorialId = tutorialId;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

}

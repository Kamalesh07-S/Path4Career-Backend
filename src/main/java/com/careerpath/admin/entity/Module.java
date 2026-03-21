package com.careerpath.admin.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import java.util.List;
import java.util.ArrayList;
import com.careerpath.admin.entity.Difficulty;
import com.careerpath.admin.entity.DurationUnit;


@Entity
@Table(name = "modules")
public class Module {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;
    
    @Column(name = "duration_value", nullable = false)
    private Integer durationValue;

    @Enumerated(EnumType.STRING)
    @Column(name = "duration_unit", nullable = false)
    private DurationUnit durationUnit;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Difficulty difficulty;

    private boolean active = true;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime lastModifiedAt;
    
    @PrePersist
    public void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.lastModifiedAt = now;
    }

    @PreUpdate
    public void onUpdate() {
        this.lastModifiedAt = LocalDateTime.now();
    }
    
    @ManyToMany
    @JoinTable(
        name = "module_skills",
        joinColumns = @JoinColumn(name = "module_id"),
        inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    private List<Skill> skills = new ArrayList<>();


    // ===== getters & setters =====

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public LocalDateTime getLastModifiedAt() {
        return lastModifiedAt;
    }
    
    public List<Skill> getSkills() {
        return skills;
    }

    public void setSkills(List<Skill> skills) {
        this.skills = skills;
    }
    
    public Integer getDurationValue() {
        return durationValue;
    }

    public void setDurationValue(Integer durationValue) {
        this.durationValue = durationValue;
    }

    public DurationUnit getDurationUnit() {
        return durationUnit;
    }

    public void setDurationUnit(DurationUnit durationUnit) {
        this.durationUnit = durationUnit;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }


}

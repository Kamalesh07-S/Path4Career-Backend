package com.careerpath.admin.entity;

import jakarta.persistence.*;

@Entity
@Table(
    name = "roadmap_modules",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"roadmap_id", "module_id"})
    }
)
public class RoadmapModule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "roadmap_id", nullable = false)
    private Roadmap roadmap;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id", nullable = false)
    private Module module;

    @Column(name = "order_index", nullable = false)
    private int orderIndex;

    // getters & setters
    public Long getId() {
        return id;
    }

    public Roadmap getRoadmap() {
        return roadmap;
    }

    public void setRoadmap(Roadmap roadmap) {
        this.roadmap = roadmap;
    }

    public Module getModule() {
        return module;
    }

    public void setModule(Module module) {
        this.module = module;
    }

    public int getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(int orderIndex) {
        this.orderIndex = orderIndex;
    }

}

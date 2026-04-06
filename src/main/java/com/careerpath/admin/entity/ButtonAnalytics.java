package com.careerpath.admin.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "button_analytics")
public class ButtonAnalytics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String buttonName;

    @Column(nullable = false)
    private Long clickCount = 0L;

    public ButtonAnalytics() {
    }

    public ButtonAnalytics(String buttonName, Long clickCount) {
        this.buttonName = buttonName;
        this.clickCount = clickCount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getButtonName() {
        return buttonName;
    }

    public void setButtonName(String buttonName) {
        this.buttonName = buttonName;
    }

    public Long getClickCount() {
        return clickCount;
    }

    public void setClickCount(Long clickCount) {
        this.clickCount = clickCount;
    }

    public void incrementClickCount() {
        this.clickCount++;
    }
}

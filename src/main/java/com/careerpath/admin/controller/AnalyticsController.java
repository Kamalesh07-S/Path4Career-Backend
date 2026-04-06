package com.careerpath.admin.controller;

import com.careerpath.admin.entity.ButtonAnalytics;
import com.careerpath.admin.repository.ButtonAnalyticsRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/analytics")
public class AnalyticsController {

    private final ButtonAnalyticsRepository buttonAnalyticsRepository;

    public AnalyticsController(ButtonAnalyticsRepository buttonAnalyticsRepository) {
        this.buttonAnalyticsRepository = buttonAnalyticsRepository;
    }

    @PostMapping("/click")
    public ResponseEntity<String> trackClick(@RequestBody Map<String, String> body) {
        String buttonName = body.get("buttonName");
        
        if (buttonName == null || buttonName.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("buttonName is required");
        }

        ButtonAnalytics analytics = buttonAnalyticsRepository.findByButtonName(buttonName)
                .orElseGet(() -> new ButtonAnalytics(buttonName, 0L));

        analytics.incrementClickCount();
        buttonAnalyticsRepository.save(analytics);

        return ResponseEntity.ok("Click registered successfully");
    }
}

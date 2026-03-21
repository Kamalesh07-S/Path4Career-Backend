package com.careerpath.admin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        // Use environment variables for production URL easily, support existing local ports, and allow all vercel.app domains for easy testing.
        String frontendUrl = System.getenv("FRONTEND_URL");
        config.setAllowedOriginPatterns(List.of(
                "http://127.0.0.1:5500", "http://localhost:5500",
                "http://127.0.0.1:5501", "http://localhost:5501",
                "http://127.0.0.1:5502", "http://localhost:5502",
                "https://*.vercel.app", "https://*.onrender.com", "https://*.up.railway.app",
                frontendUrl != null ? frontendUrl : "http://localhost:8080"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "Role", "X-Requested-With", "Cookie"));
        config.setExposedHeaders(List.of("Set-Cookie"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}

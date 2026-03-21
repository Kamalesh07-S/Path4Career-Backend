package com.careerpath.admin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.careerpath.admin.security.JwtFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .cors(cors -> {
                })
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Preflight
                        .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()
                        // Admin login
                        .requestMatchers("/api/login").permitAll()
                        // User auth (register, login, logout)
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        // Public endpoints (courses, search)
                        .requestMatchers("/api/public/**").permitAll()
                        // User-facing read-only endpoints
                        .requestMatchers("/api/user/**").permitAll()
                        // Admin API endpoints — method-level @PreAuthorize
                        // handles role checks for write operations
                        .requestMatchers("/api/roadmaps/**").permitAll()
                        .requestMatchers("/api/modules/**").permitAll()
                        .requestMatchers("/api/skills/**").permitAll()
                        .requestMatchers("/api/users/**").permitAll()
                        .requestMatchers("/api/jobs/**").permitAll()
                        .requestMatchers("/api/applications/**").permitAll()
                        .requestMatchers("/api/admins/**").permitAll()
                        .requestMatchers("/api/job-roles/**").permitAll()
                        .requestMatchers("/api/quiz/list", "/api/quiz/{tutorialId}").permitAll()
                        // User dashboard & activities (require user JWT)
                        .requestMatchers("/api/dashboard/**").authenticated()
                        .requestMatchers("/api/v1/user/**").authenticated()
                        .requestMatchers("/api/v1/activities/**").authenticated()
                        .requestMatchers("/api/v1/tutorials/**").authenticated()
                        // Certificate endpoints
                        .requestMatchers("/api/v1/certificates/verify/**").permitAll()
                        .requestMatchers("/api/v1/certificates/**").authenticated()
                        // Everything else
                        .anyRequest().permitAll())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}

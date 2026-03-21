package com.careerpath.admin.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.List;

import io.jsonwebtoken.Claims;

import java.io.IOException;

/**
 * Unified JWT filter that supports BOTH:
 * 1) Admin tokens (role-based, hardcoded secret via JwtUtil)
 * 2) User tokens (email-based, configurable secret via UserJwtUtil)
 *
 * Token is read from Authorization header (Bearer) or AUTH_TOKEN cookie.
 * Admin JwtUtil is tried first; if invalid, UserJwtUtil is tried.
 */
@Component
public class JwtFilter extends OncePerRequestFilter {

        @Autowired
        private JwtUtil jwtUtil;

        @Autowired
        private UserJwtUtil userJwtUtil;

        @Autowired
        private CustomUserDetailsService customUserDetailsService;

        @Override
        protected boolean shouldNotFilter(HttpServletRequest request) {
                return "OPTIONS".equalsIgnoreCase(request.getMethod());
        }

        @Override
        protected void doFilterInternal(
                        HttpServletRequest request,
                        HttpServletResponse response,
                        FilterChain chain)
                        throws ServletException, IOException {

                // 1. Extract token from Authorization header or AUTH_TOKEN cookie
                String token = extractToken(request);

                if (token != null) {
                        // 2. Try ADMIN token first (hardcoded secret, has "role" claim)
                        if (jwtUtil.isTokenValid(token)) {
                                authenticateAdmin(token, request);
                        }
                        // 3. Try USER token (configurable secret)
                        else if (userJwtUtil.isTokenValid(token)) {
                                authenticateUser(token, request);
                        }
                }

                chain.doFilter(request, response);
        }

        private String extractToken(HttpServletRequest request) {
                // From Authorization header
                String authHeader = request.getHeader("Authorization");
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                        return authHeader.substring(7);
                }

                // From AUTH_TOKEN cookie
                if (request.getCookies() != null) {
                        for (var cookie : request.getCookies()) {
                                if ("AUTH_TOKEN".equals(cookie.getName())) {
                                        return cookie.getValue();
                                }
                        }
                }

                return null;
        }

        /**
         * Admin auth: extract role from JWT claim, set authority directly.
         */
        private void authenticateAdmin(String token, HttpServletRequest request) {
                try {
                        Claims claims = jwtUtil.extractClaims(token);
                        String username = claims.getSubject();
                        String role = claims.get("role", String.class);
                        String authority = "ROLE_" + role;

                        List<SimpleGrantedAuthority> authorities = List.of(
                                        new SimpleGrantedAuthority(authority));

                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                        username, null, authorities);
                        authentication.setDetails(
                                        new WebAuthenticationDetailsSource().buildDetails(request));

                        SecurityContextHolder.getContext().setAuthentication(authentication);
                } catch (Exception e) {
                        SecurityContextHolder.clearContext();
                }
        }

        /**
         * User auth: extract email from JWT, load full UserDetails from DB.
         */
        private void authenticateUser(String token, HttpServletRequest request) {
                try {
                        String email = userJwtUtil.extractEmail(token);
                        UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                        userDetails, null, userDetails.getAuthorities());
                        authentication.setDetails(
                                        new WebAuthenticationDetailsSource().buildDetails(request));

                        SecurityContextHolder.getContext().setAuthentication(authentication);
                } catch (Exception e) {
                        SecurityContextHolder.clearContext();
                }
        }
}

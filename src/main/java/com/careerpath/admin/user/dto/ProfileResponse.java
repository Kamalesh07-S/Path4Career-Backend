package com.careerpath.admin.user.dto;

import java.time.LocalDate;

public class ProfileResponse {
    private String username;
    private String email;
    private String role;
    private String fullName;
    private String phone;
    private LocalDate dob;
    private String gender;
    private String location;

    public ProfileResponse(String username, String email, String role) {
        this.username = username;
        this.email = email;
        this.role = role;
    }

    public ProfileResponse(String username, String email, String role,
            String fullName, String phone, LocalDate dob,
            String gender, String location) {
        this.username = username;
        this.email = email;
        this.role = role;
        this.fullName = fullName;
        this.phone = phone;
        this.dob = dob;
        this.gender = gender;
        this.location = location;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPhone() {
        return phone;
    }

    public LocalDate getDob() {
        return dob;
    }

    public String getGender() {
        return gender;
    }

    public String getLocation() {
        return location;
    }
}

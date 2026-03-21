package com.careerpath.admin.dto;

public class CreateAdminRequest {

    private String username;
    private String email;
    private String password;
    private String role;

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }
}

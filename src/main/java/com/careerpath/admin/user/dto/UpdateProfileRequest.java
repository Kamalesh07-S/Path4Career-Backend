package com.careerpath.admin.user.dto;

import java.time.LocalDate;

public class UpdateProfileRequest {
    private String fullName;
    private String phone;
    private LocalDate dob;
    private String gender;
    private String location;

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

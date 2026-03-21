package com.careerpath.admin.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="admins")
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String username;
    private String email;
    @JsonIgnore
    private String password;
    private String role;
    private Boolean active;

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
    
    public Boolean getActive() {
        return active;
    }
    
    public Integer getId() {
        return id;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

}

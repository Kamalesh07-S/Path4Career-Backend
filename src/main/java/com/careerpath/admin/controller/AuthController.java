package com.careerpath.admin.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.careerpath.admin.entity.Admin;
import com.careerpath.admin.repository.AdminRepo;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.careerpath.admin.security.JwtUtil;

@RestController
@CrossOrigin
public class AuthController {

    @Autowired
    private AdminRepo repo;

    @Autowired
    private PasswordEncoder encoder;
    
    @Autowired
    private JwtUtil jwtUtil;

    
    @PostMapping("/api/login")
    public ResponseEntity<?> login(@RequestBody Map<String,String> data){

        String identifier = data.get("username");
        String password = data.get("password");

        Admin admin;

        if(identifier.contains("@")){
            admin = repo.findByEmail(identifier).orElse(null);
        } else {
            admin = repo.findByUsername(identifier).orElse(null);
        }

        if(admin == null){
            return ResponseEntity.badRequest().body("User not found");
        }

        if(admin.getActive() == null || !admin.getActive()){
            return ResponseEntity.badRequest().body("Account deactivated");
        }

        if(!encoder.matches(password, admin.getPassword())){
            return ResponseEntity.badRequest().body("Invalid password");
        }

        String token = jwtUtil.generateToken(
                admin.getUsername(),
                admin.getRole()
        );

        Map<String,String> res = new HashMap<>();
        res.put("token", token);
        res.put("username", admin.getUsername());
        res.put("role", admin.getRole());

        return ResponseEntity.ok(res);

    }
}

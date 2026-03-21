package com.careerpath.admin.controller;

import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.careerpath.admin.entity.User;
import com.careerpath.admin.repository.UserRepo;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserRepo userRepo;
    
    @Autowired
    private PasswordEncoder encoder;

    @PreAuthorize("hasAnyRole('SUPER_ADMIN','SUPPORT_ADMIN','CONTENT_ADMIN')")
    @GetMapping
    public List<User> getAllUsers() {
        return userRepo.findAll();
    }
    
    @GetMapping("/search")
    public List<User> searchUsers(@RequestParam String q) {
        return userRepo.findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(q, q);
    }
    
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','SUPPORT_ADMIN','CONTENT_ADMIN','JOB_ADMIN')")
    @GetMapping("/count")
    public long getUserCount() {
        return userRepo.count();
    }
    
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','SUPPORT_ADMIN','CONTENT_ADMIN','JOB_ADMIN')")
    @GetMapping("/status-count")
    public Map<String, Long> getUserStatusCount() {

        Map<String, Long> res = new HashMap<>();

        res.put("active", userRepo.countByEnabled(true));
        res.put("disabled", userRepo.countByEnabled(false));

        return res;
    }

    @GetMapping("/roles")
    public List<String> getUserRoles(){
        return userRepo.findDistinctRoles();
    }
    
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','SUPPORT_ADMIN')")
    @PostMapping
    public User createUser(@RequestBody User user) {

        // default password = username + 123
        String rawPassword = user.getUsername() + "123";

        user.setPassword(encoder.encode(rawPassword));
        user.setEnabled(true);
        user.setLoggedIn(false);
        user.setCreatedAt(java.time.LocalDateTime.now());

        return userRepo.save(user);
    }
    
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','SUPPORT_ADMIN')")
    @PutMapping("/{id}/toggle")
    public ResponseEntity<?> toggleUser(@PathVariable Long id) {

        User user = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setEnabled(!user.isEnabled());

        return ResponseEntity.ok(userRepo.save(user));
    }

    
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','SUPPORT_ADMIN')")
    @PutMapping("/{id}/role")
    public ResponseEntity<?> updateUserRole(
            @PathVariable Long id,
            @RequestBody Map<String,String> body,
            @RequestHeader(value = "Role", required = false) String roleHeader
    ){
        String newRole = body.get("role");

        if(newRole == null || newRole.isEmpty()){
            return ResponseEntity.badRequest().body("Role is required");
        }

        User user = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setRole(newRole);
        userRepo.save(user);

        return ResponseEntity.ok(user);
    }



}

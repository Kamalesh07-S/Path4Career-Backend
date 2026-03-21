package com.careerpath.admin.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.careerpath.admin.entity.Admin;
import com.careerpath.admin.repository.AdminRepo;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.careerpath.admin.dto.CreateAdminRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Map;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.access.prepost.PreAuthorize;


@RestController
@RequestMapping("/api")

public class AdminController {

    private final AdminRepo repo;
    private final PasswordEncoder passwordEncoder;

    public AdminController(AdminRepo repo, PasswordEncoder passwordEncoder) {
        this.repo = repo;
        this.passwordEncoder = passwordEncoder;
    }
    
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @GetMapping("/admins")
    public ResponseEntity<?> getAllAdmins(){

        List<Admin> admins = repo.findAll();
        return ResponseEntity.ok(admins);
    }
    
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @GetMapping("/admins/count")
    public long getAdminCount() {
        return repo.count();
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PostMapping("/admins")
    public ResponseEntity<?> createAdmin(@RequestBody CreateAdminRequest req) {
    	
    	if(repo.existsByEmail(req.getEmail())){
    	    return ResponseEntity
    	            .status(HttpStatus.BAD_REQUEST)
    	            .body("Email already exists");
    	}

        Admin admin = new Admin();
        
        admin.setUsername(req.getUsername());
        admin.setEmail(req.getEmail());
        admin.setRole(req.getRole());
        admin.setPassword(passwordEncoder.encode(req.getPassword()));
        admin.setActive(true);

        
        return ResponseEntity.ok(repo.save(admin));

    }
    
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PutMapping("/admins/{id}/toggle")
    public ResponseEntity<?> toggleAdmin(
            @PathVariable Integer id
    ) {

        Admin admin = repo.findById(id).orElse(null);
        
     // Prevent super admin self-deactivation
        if(admin.getRole().equals("SUPER_ADMIN") && admin.getId().equals(id)){
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("Super Admin cannot deactivate themselves");
        }

        if (admin == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Admin not found");
        }

        // toggle active
        admin.setActive(!Boolean.TRUE.equals(admin.getActive()));

        repo.save(admin);

        return ResponseEntity.ok(admin.getActive() ? "Admin activated" : "Admin deactivated");
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PutMapping("/admins/{id}/role")
    public ResponseEntity<?> updateAdminRole(
            @PathVariable Integer id,
            @RequestBody Map<String, String> body,
            HttpServletRequest request
    ) {


        String newRole = body.get("role");

        Admin admin = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Admin not found"));
        String currentAdmin = request.getHeader("Role");

        if(admin.getRole().equals("SUPER_ADMIN")){
            return ResponseEntity
                .badRequest()
                .body("Super Admin role cannot be modified");
        }


        admin.setRole(newRole);
        repo.save(admin);


        return ResponseEntity.ok("Role updated");
    }

}

package com.careerpath.admin.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.careerpath.admin.entity.Admin;

public interface AdminRepo extends JpaRepository<Admin,Integer> {

    Optional<Admin> findByUsername(String username);

    Optional<Admin> findByEmail(String email);
    
    boolean existsByEmail(String email);

}

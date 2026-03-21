package com.careerpath.admin.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.careerpath.admin.entity.User;
import org.springframework.data.jpa.repository.Query;

public interface UserRepo extends JpaRepository<User, Long> {

    User findByEmail(String email);
    
    List<User> findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(
        String username,
        String email
    );
    
    long countByEnabled(boolean enabled);
    
    @Query("SELECT DISTINCT u.role FROM User u")
    List<String> findDistinctRoles();

}

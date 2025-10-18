package com.morago_backend.repository;

import com.morago_backend.entity.UserRole;
import com.morago_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByPhone(String phone);
    List<User> findByRoles(UserRole role);
    
    // Account status queries
    List<User> findByIsActive(Boolean isActive);
    
    @Query("SELECT u FROM User u WHERE u.isActive = true")
    List<User> findAllActiveUsers();
    
    @Query("SELECT u FROM User u WHERE u.isActive = false")
    List<User> findAllBlockedUsers();
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.isActive = true")
    Long countActiveUsers();
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.isActive = false")
    Long countBlockedUsers();
}

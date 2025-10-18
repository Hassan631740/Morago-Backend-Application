package com.morago_backend.repository;

import com.morago_backend.entity.Deposit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DepositRepository extends JpaRepository<Deposit, Long> {
    
    // ========== QUERY BY USER ==========
    
    /**
     * Find all deposits for a specific user, sorted by date (newest first)
     */
    List<Deposit> findByUserIdOrderByCreatedAtDatetimeDesc(Long userId);
    
    /**
     * Find deposits for a specific user with pagination and date sorting
     */
    Page<Deposit> findByUserIdOrderByCreatedAtDatetimeDesc(Long userId, Pageable pageable);
    
    // ========== QUERY BY STATUS ==========
    
    /**
     * Find deposits by status, sorted by date (newest first)
     */
    List<Deposit> findByStatusOrderByCreatedAtDatetimeDesc(String status);
    
    /**
     * Find deposits by status with pagination
     */
    Page<Deposit> findByStatusOrderByCreatedAtDatetimeDesc(String status, Pageable pageable);
    
    // ========== QUERY BY USER AND STATUS ==========
    
    /**
     * Find deposits for a user with specific status
     */
    List<Deposit> findByUserIdAndStatusOrderByCreatedAtDatetimeDesc(Long userId, String status);
    
    /**
     * Find deposits for a user with specific status (paginated)
     */
    Page<Deposit> findByUserIdAndStatusOrderByCreatedAtDatetimeDesc(Long userId, String status, Pageable pageable);
    
    // ========== DATE RANGE QUERIES ==========
    
    /**
     * Find deposits within a date range
     */
    @Query("SELECT d FROM Deposit d WHERE d.createdAtDatetime BETWEEN :startDate AND :endDate " +
           "ORDER BY d.createdAtDatetime DESC")
    List<Deposit> findByDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
    
    /**
     * Find deposits for a user within a date range
     */
    @Query("SELECT d FROM Deposit d WHERE d.userId = :userId " +
           "AND d.createdAtDatetime BETWEEN :startDate AND :endDate " +
           "ORDER BY d.createdAtDatetime DESC")
    Page<Deposit> findByUserIdAndDateRange(
            @Param("userId") Long userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );
    
    /**
     * Find deposits by status within a date range
     */
    @Query("SELECT d FROM Deposit d WHERE d.status = :status " +
           "AND d.createdAtDatetime BETWEEN :startDate AND :endDate " +
           "ORDER BY d.createdAtDatetime DESC")
    Page<Deposit> findByStatusAndDateRange(
            @Param("status") String status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );
    
    // ========== AGGREGATION QUERIES ==========
    
    /**
     * Get total deposit amount for a user (approved only)
     */
    @Query("SELECT COALESCE(SUM(d.sum), 0) FROM Deposit d " +
           "WHERE d.userId = :userId AND d.status = 'APPROVED'")
    BigDecimal getTotalApprovedDepositsByUserId(@Param("userId") Long userId);
    
    /**
     * Get total deposit amount by status
     */
    @Query("SELECT COALESCE(SUM(d.sum), 0) FROM Deposit d WHERE d.status = :status")
    BigDecimal getTotalDepositsByStatus(@Param("status") String status);
    
    /**
     * Count deposits by user
     */
    long countByUserId(Long userId);
    
    /**
     * Count deposits by user and status
     */
    long countByUserIdAndStatus(Long userId, String status);
    
    /**
     * Check if user has pending deposits
     */
    boolean existsByUserIdAndStatus(Long userId, String status);
}



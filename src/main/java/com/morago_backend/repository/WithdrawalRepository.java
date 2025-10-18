package com.morago_backend.repository;

import com.morago_backend.entity.Withdrawal;
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
public interface WithdrawalRepository extends JpaRepository<Withdrawal, Long> {
    
    // ========== QUERY BY USER ==========
    
    /**
     * Find all withdrawals for a specific user, sorted by date (newest first)
     */
    List<Withdrawal> findByUserIdOrderByCreatedAtDatetimeDesc(Long userId);
    
    /**
     * Find withdrawals for a specific user with pagination and date sorting
     */
    Page<Withdrawal> findByUserIdOrderByCreatedAtDatetimeDesc(Long userId, Pageable pageable);
    
    // ========== QUERY BY STATUS ==========
    
    /**
     * Find withdrawals by status, sorted by date (newest first)
     */
    List<Withdrawal> findByStatusOrderByCreatedAtDatetimeDesc(String status);
    
    /**
     * Find withdrawals by status with pagination
     */
    Page<Withdrawal> findByStatusOrderByCreatedAtDatetimeDesc(String status, Pageable pageable);
    
    // ========== QUERY BY USER AND STATUS ==========
    
    /**
     * Find withdrawals for a user with specific status
     */
    List<Withdrawal> findByUserIdAndStatusOrderByCreatedAtDatetimeDesc(Long userId, String status);
    
    /**
     * Find withdrawals for a user with specific status (paginated)
     */
    Page<Withdrawal> findByUserIdAndStatusOrderByCreatedAtDatetimeDesc(Long userId, String status, Pageable pageable);
    
    // ========== DATE RANGE QUERIES ==========
    
    /**
     * Find withdrawals within a date range
     */
    @Query("SELECT w FROM Withdrawal w WHERE w.createdAtDatetime BETWEEN :startDate AND :endDate " +
           "ORDER BY w.createdAtDatetime DESC")
    List<Withdrawal> findByDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
    
    /**
     * Find withdrawals for a user within a date range
     */
    @Query("SELECT w FROM Withdrawal w WHERE w.userId = :userId " +
           "AND w.createdAtDatetime BETWEEN :startDate AND :endDate " +
           "ORDER BY w.createdAtDatetime DESC")
    Page<Withdrawal> findByUserIdAndDateRange(
            @Param("userId") Long userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );
    
    /**
     * Find withdrawals by status within a date range
     */
    @Query("SELECT w FROM Withdrawal w WHERE w.status = :status " +
           "AND w.createdAtDatetime BETWEEN :startDate AND :endDate " +
           "ORDER BY w.createdAtDatetime DESC")
    Page<Withdrawal> findByStatusAndDateRange(
            @Param("status") String status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );
    
    // ========== AGGREGATION QUERIES ==========
    
    /**
     * Get total withdrawal amount for a user (approved only)
     */
    @Query("SELECT COALESCE(SUM(w.sum), 0) FROM Withdrawal w " +
           "WHERE w.userId = :userId AND w.status = 'APPROVED'")
    BigDecimal getTotalApprovedWithdrawalsByUserId(@Param("userId") Long userId);
    
    /**
     * Get total withdrawal amount by status
     */
    @Query("SELECT COALESCE(SUM(w.sum), 0) FROM Withdrawal w WHERE w.status = :status")
    BigDecimal getTotalWithdrawalsByStatus(@Param("status") String status);
    
    /**
     * Get total pending withdrawal amount for a user
     */
    @Query("SELECT COALESCE(SUM(w.sum), 0) FROM Withdrawal w " +
           "WHERE w.userId = :userId AND w.status = 'PENDING'")
    BigDecimal getTotalPendingWithdrawalsByUserId(@Param("userId") Long userId);
    
    /**
     * Count withdrawals by user
     */
    long countByUserId(Long userId);
    
    /**
     * Count withdrawals by user and status
     */
    long countByUserIdAndStatus(Long userId, String status);
    
    /**
     * Check if user has pending withdrawals
     */
    boolean existsByUserIdAndStatus(Long userId, String status);
}



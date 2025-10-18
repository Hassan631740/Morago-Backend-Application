package com.morago_backend.repository;

import com.morago_backend.entity.Transaction;
import com.morago_backend.entity.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // Find all transactions for a specific user
    Page<Transaction> findByUserIdOrderByCreatedAtDatetimeDesc(Long userId, Pageable pageable);

    // Find transactions by user and type
    Page<Transaction> findByUserIdAndTransactionTypeOrderByCreatedAtDatetimeDesc(
            Long userId, 
            TransactionType transactionType, 
            Pageable pageable
    );

    // Find transactions by user and status
    Page<Transaction> findByUserIdAndStatusOrderByCreatedAtDatetimeDesc(
            Long userId, 
            String status, 
            Pageable pageable
    );

    // Find transactions within date range
    @Query("SELECT t FROM Transaction t WHERE t.user.id = :userId " +
           "AND t.createdAtDatetime BETWEEN :startDate AND :endDate " +
           "ORDER BY t.createdAtDatetime DESC")
    Page<Transaction> findByUserIdAndDateRange(
            @Param("userId") Long userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );

    // Get all transactions for a user (without pagination)
    List<Transaction> findByUserIdOrderByCreatedAtDatetimeDesc(Long userId);

    // Find transaction by related entity
    List<Transaction> findByDepositId(Long depositId);
    List<Transaction> findByWithdrawalId(Long withdrawalId);
    List<Transaction> findByCallRecordId(Long callRecordId);
    List<Transaction> findByDebtorId(Long debtorId);

    // Calculate total by type for a user
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
           "WHERE t.user.id = :userId AND t.transactionType = :type AND t.status = 'COMPLETED'")
    java.math.BigDecimal sumByUserIdAndType(
            @Param("userId") Long userId, 
            @Param("type") TransactionType type
    );

    // Count transactions by user
    long countByUserId(Long userId);
}


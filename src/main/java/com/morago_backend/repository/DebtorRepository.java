package com.morago_backend.repository;

import com.morago_backend.entity.Debtor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface DebtorRepository extends JpaRepository<Debtor, Long> {
    
    // Find all unpaid debts for a user
    List<Debtor> findByUserIdAndPaidFalse(Long userId);
    
    // Find all debts for a user
    List<Debtor> findByUserId(Long userId);
    
    // Count unpaid debts for a user
    long countByUserIdAndPaidFalse(Long userId);
    
    // Check if user has any unpaid debts
    boolean existsByUserIdAndPaidFalse(Long userId);
    
    // Get total debt amount for a user
    @Query("SELECT COALESCE(SUM(d.debtAmount), 0) FROM Debtor d WHERE d.userId = :userId AND d.paid = false")
    BigDecimal getTotalDebtByUserId(@Param("userId") Long userId);
}



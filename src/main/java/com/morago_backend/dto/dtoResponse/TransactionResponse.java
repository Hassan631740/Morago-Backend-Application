package com.morago_backend.dto.dtoResponse;

import com.morago_backend.entity.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {
    private Long id;
    private Long userId;
    private String userName;
    private TransactionType transactionType;
    private BigDecimal amount;
    private BigDecimal balanceBefore;
    private BigDecimal balanceAfter;
    private String status;
    private String description;
    private Long depositId;
    private Long withdrawalId;
    private Long callRecordId;
    private Long debtorId;
    private String accountHolder;
    private String bankName;
    private String accountNumber;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}


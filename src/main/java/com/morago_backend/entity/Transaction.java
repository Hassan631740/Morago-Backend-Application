package com.morago_backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
@Entity
@Table(name = "transactions")
public class Transaction extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false, length = 50)
    private TransactionType transactionType;

    @Column(name = "amount", precision = 12, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column(name = "balance_before", precision = 12, scale = 2)
    private BigDecimal balanceBefore;

    @Column(name = "balance_after", precision = 12, scale = 2)
    private BigDecimal balanceAfter;

    @Column(name = "status", length = 50)
    private String status;  // PENDING, COMPLETED, FAILED, REJECTED

    @Column(name = "description", length = 500)
    private String description;

    // Reference to related entities
    @Column(name = "deposit_id")
    private Long depositId;

    @Column(name = "withdrawal_id")
    private Long withdrawalId;

    @Column(name = "call_record_id")
    private Long callRecordId;

    @Column(name = "debtor_id")
    private Long debtorId;

    // Additional metadata
    @Column(name = "account_holder", length = 200)
    private String accountHolder;

    @Column(name = "bank_name", length = 200)
    private String bankName;

    @Column(name = "account_number", length = 200)
    private String accountNumber;

    @Column(name = "notes", length = 1000)
    private String notes;

}


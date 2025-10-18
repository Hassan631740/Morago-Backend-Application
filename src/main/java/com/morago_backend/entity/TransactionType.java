package com.morago_backend.entity;

public enum TransactionType {
    DEPOSIT,           // Money added to account
    WITHDRAWAL,        // Money withdrawn from account
    CALL_PAYMENT,      // Payment for a call (debit)
    CALL_EARNING,      // Earning from translating a call (credit)
    COMMISSION,        // Platform commission (debit)
    REFUND,            // Refund to user
    ADJUSTMENT         // Manual adjustment by admin
}


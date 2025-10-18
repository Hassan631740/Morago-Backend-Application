package com.morago_backend.service;

import com.morago_backend.dto.dtoRequest.TransactionFilterRequest;
import com.morago_backend.dto.dtoResponse.TransactionResponse;
import com.morago_backend.entity.*;
import com.morago_backend.repository.TransactionRepository;
import com.morago_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    /**
     * Create a new transaction record
     */
    @Transactional
    public Transaction createTransaction(
            User user,
            TransactionType transactionType,
            BigDecimal amount,
            String status,
            String description
    ) {
        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setTransactionType(transactionType);
        transaction.setAmount(amount);
        transaction.setBalanceBefore(user.getBalance());
        transaction.setStatus(status);
        transaction.setDescription(description);

        // Calculate balance after based on transaction type
        BigDecimal balanceAfter = calculateBalanceAfter(user.getBalance(), amount, transactionType);
        transaction.setBalanceAfter(balanceAfter);

        return transactionRepository.save(transaction);
    }

    /**
     * Create transaction with additional details
     */
    @Transactional
    public Transaction createDetailedTransaction(
            User user,
            TransactionType transactionType,
            BigDecimal amount,
            String status,
            String description,
            Long relatedEntityId,
            String accountHolder,
            String bankName,
            String accountNumber,
            String notes
    ) {
        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setTransactionType(transactionType);
        transaction.setAmount(amount);
        transaction.setBalanceBefore(user.getBalance());
        transaction.setStatus(status);
        transaction.setDescription(description);
        transaction.setAccountHolder(accountHolder);
        transaction.setBankName(bankName);
        transaction.setAccountNumber(accountNumber);
        transaction.setNotes(notes);

        // Set related entity ID based on transaction type
        switch (transactionType) {
            case DEPOSIT -> transaction.setDepositId(relatedEntityId);
            case WITHDRAWAL -> transaction.setWithdrawalId(relatedEntityId);
            case CALL_PAYMENT, CALL_EARNING, COMMISSION -> transaction.setCallRecordId(relatedEntityId);
        }

        // Calculate balance after
        BigDecimal balanceAfter = calculateBalanceAfter(user.getBalance(), amount, transactionType);
        transaction.setBalanceAfter(balanceAfter);

        Transaction savedTransaction = transactionRepository.save(transaction);
        log.info("Created transaction: type={}, amount={}, userId={}", 
                transactionType, amount, user.getId());
        
        return savedTransaction;
    }

    /**
     * Get all transactions for a user with pagination
     */
    @Transactional(readOnly = true)
    public Page<TransactionResponse> getUserTransactions(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Transaction> transactions = transactionRepository.findByUserIdOrderByCreatedAtDatetimeDesc(userId, pageable);
        return transactions.map(this::convertToResponse);
    }

    /**
     * Get filtered transactions for a user
     */
    @Transactional(readOnly = true)
    public Page<TransactionResponse> getFilteredTransactions(Long userId, TransactionFilterRequest filter) {
        Pageable pageable = PageRequest.of(filter.getPage(), filter.getSize());
        Page<Transaction> transactions;

        if (filter.getStartDate() != null && filter.getEndDate() != null) {
            transactions = transactionRepository.findByUserIdAndDateRange(
                    userId, filter.getStartDate(), filter.getEndDate(), pageable
            );
        } else if (filter.getTransactionType() != null) {
            transactions = transactionRepository.findByUserIdAndTransactionTypeOrderByCreatedAtDatetimeDesc(
                    userId, filter.getTransactionType(), pageable
            );
        } else if (filter.getStatus() != null) {
            transactions = transactionRepository.findByUserIdAndStatusOrderByCreatedAtDatetimeDesc(
                    userId, filter.getStatus(), pageable
            );
        } else {
            transactions = transactionRepository.findByUserIdOrderByCreatedAtDatetimeDesc(userId, pageable);
        }

        return transactions.map(this::convertToResponse);
    }

    /**
     * Get transaction by ID
     */
    @Transactional(readOnly = true)
    public TransactionResponse getTransactionById(Long transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found with id: " + transactionId));
        return convertToResponse(transaction);
    }

    /**
     * Get total amounts by transaction type for a user
     */
    @Transactional(readOnly = true)
    public BigDecimal getTotalByType(Long userId, TransactionType type) {
        return transactionRepository.sumByUserIdAndType(userId, type);
    }

    /**
     * Get transaction count for a user
     */
    @Transactional(readOnly = true)
    public long getTransactionCount(Long userId) {
        return transactionRepository.countByUserId(userId);
    }

    /**
     * Calculate balance after transaction
     */
    private BigDecimal calculateBalanceAfter(BigDecimal currentBalance, BigDecimal amount, TransactionType type) {
        return switch (type) {
            case DEPOSIT, CALL_EARNING, REFUND -> currentBalance.add(amount);
            case WITHDRAWAL, CALL_PAYMENT, COMMISSION -> currentBalance.subtract(amount);
            case ADJUSTMENT -> amount; // For adjustments, amount is the new balance
        };
    }

    /**
     * Convert Transaction entity to TransactionResponse DTO
     */
    private TransactionResponse convertToResponse(Transaction transaction) {
        return TransactionResponse.builder()
                .id(transaction.getId())
                .userId(transaction.getUser().getId())
                .userName(transaction.getUser().getFirstName() + " " + transaction.getUser().getLastName())
                .transactionType(transaction.getTransactionType())
                .amount(transaction.getAmount())
                .balanceBefore(transaction.getBalanceBefore())
                .balanceAfter(transaction.getBalanceAfter())
                .status(transaction.getStatus())
                .description(transaction.getDescription())
                .depositId(transaction.getDepositId())
                .withdrawalId(transaction.getWithdrawalId())
                .callRecordId(transaction.getCallRecordId())
                .debtorId(transaction.getDebtorId())
                .accountHolder(transaction.getAccountHolder())
                .bankName(transaction.getBankName())
                .accountNumber(transaction.getAccountNumber())
                .notes(transaction.getNotes())
                .createdAt(transaction.getCreatedAtDatetime())
                .updatedAt(transaction.getUpdatedAtDatetime())
                .build();
    }
}


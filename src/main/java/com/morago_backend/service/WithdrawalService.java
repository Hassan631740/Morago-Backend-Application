package com.morago_backend.service;

import com.morago_backend.dto.dtoResponse.WithdrawalResponseDTO;
import com.morago_backend.entity.TransactionType;
import com.morago_backend.entity.User;
import com.morago_backend.entity.UserRole;
import com.morago_backend.entity.Withdrawal;
import com.morago_backend.repository.UserRepository;
import com.morago_backend.repository.WithdrawalRepository;
import com.corundumstudio.socketio.SocketIOServer;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WithdrawalService {

    private final WithdrawalRepository repository;
    private final UserRepository userRepository;
    private final SocketIOServer socketServer;
    private final TransactionService transactionService;

    private static final Logger logger = LoggerFactory.getLogger(WithdrawalService.class);

    //=== Translator request withdrawal ===//
    @Transactional
    public WithdrawalResponseDTO requestWithdrawal(BigDecimal sum, String accountNumber, String accountHolder, String bankName) {
        String phone = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByPhone(phone)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getRoles().contains(UserRole.INTERPRETER)) {
            throw new RuntimeException("Only translators can request withdrawal");
        }

        if (sum.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        // Check if user has unpaid debts
        if (Boolean.TRUE.equals(user.getIsDebtor())) {
            throw new RuntimeException("Cannot request withdrawal while having unpaid debts. Please settle your debts first.");
        }

        Withdrawal w = new Withdrawal();
        w.setUserId(user.getId());
        w.setSum(sum);
        w.setAccountNumber(accountNumber);
        w.setAccountHolder(accountHolder);
        w.setBankName(bankName);
        w.setStatus("PENDING");

        Withdrawal saved = repository.save(w);
        socketServer.getBroadcastOperations().sendEvent("withdrawalRequested", saved);
        logger.info("Withdrawal requested for userId={} sum={}", user.getId(), sum);
        return mapToResponse(saved);
    }

    //=== Admin approve/reject withdrawal ===//
    @Transactional
    public WithdrawalResponseDTO approveOrReject(Long withdrawalId, String status) {
        Withdrawal w = repository.findById(withdrawalId)
                .orElseThrow(() -> new RuntimeException("Withdrawal not found"));

        if (!status.equalsIgnoreCase("APPROVED") && !status.equalsIgnoreCase("REJECTED")) {
            throw new IllegalArgumentException("Status must be APPROVED or REJECTED");
        }

        String prevStatus = w.getStatus();
        w.setStatus(status);
        Withdrawal saved = repository.save(w);

        if ("APPROVED".equalsIgnoreCase(status) && !"APPROVED".equalsIgnoreCase(prevStatus)) {
            User user = userRepository.findById(w.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            BigDecimal balance = user.getBalance() == null ? BigDecimal.ZERO : user.getBalance();
            if (balance.compareTo(w.getSum()) < 0) {
                throw new RuntimeException("Insufficient balance for withdrawal");
            }
            user.setBalance(balance.subtract(w.getSum()));
            user = userRepository.save(user);
            
            // Create transaction record
            transactionService.createDetailedTransaction(
                user,
                TransactionType.WITHDRAWAL,
                saved.getSum(),
                "COMPLETED",
                "Withdrawal approved and debited from account",
                saved.getId(),
                saved.getAccountHolder(),
                saved.getBankName(),
                saved.getAccountNumber(),
                "Withdrawal ID: " + saved.getId()
            );
            logger.info("Transaction record created for withdrawal id={}", saved.getId());
        }

        socketServer.getBroadcastOperations().sendEvent("withdrawalUpdated", saved);
        logger.info("Withdrawal id={} updated status={}", withdrawalId, status);
        return mapToResponse(saved);
    }

    //=== Admin get all withdrawals ===//
    public List<WithdrawalResponseDTO> findAll() {
        return repository.findAll().stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    //=== Admin get withdrawal by ID ===//
    public Optional<WithdrawalResponseDTO> findById(Long id) {
        return repository.findById(id).map(this::mapToResponse);
    }

    //=== Admin delete withdrawal ===//
    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
        socketServer.getBroadcastOperations().sendEvent("withdrawalDeleted", id);
        logger.info("Withdrawal deleted id={}", id);
    }

    private WithdrawalResponseDTO mapToResponse(Withdrawal w) {
        return new WithdrawalResponseDTO(
                w.getId(),
                w.getAccountNumber(),
                w.getAccountHolder(),
                w.getBankName(),
                w.getSum(),
                w.getStatus(),
                w.getUserId(),
                w.getCreatedAtDatetime(),
                w.getUpdatedAtDatetime()
        );
    }
}

package com.morago_backend.service;

import com.morago_backend.dto.dtoRequest.DepositRequestDTO;
import com.morago_backend.dto.dtoResponse.DepositResponseDTO;
import com.morago_backend.entity.Debtor;
import com.morago_backend.entity.Deposit;
import com.morago_backend.entity.TransactionType;
import com.morago_backend.entity.User;
import com.morago_backend.repository.DebtorRepository;
import com.morago_backend.repository.DepositRepository;
import com.morago_backend.repository.UserRepository;
import com.morago_backend.exception.ResourceNotFoundException;
import com.corundumstudio.socketio.SocketIOServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DepositService {

    private static final Logger logger = LoggerFactory.getLogger(DepositService.class);

    private final DepositRepository depositRepository;
    private final SocketIOServer socketServer;
    private final UserRepository userRepository;
    private final TransactionService transactionService;
    private final DebtorRepository debtorRepository;

    public DepositService(DepositRepository depositRepository, SocketIOServer socketServer, 
                         UserRepository userRepository, TransactionService transactionService,
                         DebtorRepository debtorRepository) {
        this.depositRepository = depositRepository;
        this.socketServer = socketServer;
        this.userRepository = userRepository;
        this.transactionService = transactionService;
        this.debtorRepository = debtorRepository;
    }

    // ====== CREATE ======
    @Transactional
    public DepositResponseDTO create(DepositRequestDTO dto) {
        try {
            logger.info("Creating deposit for userId {}", dto.getUserId());
            Deposit deposit = mapToEntity(dto);
            validatePositiveAmount(deposit.getSum());
            Deposit saved = depositRepository.save(deposit);

            if ("APPROVED".equalsIgnoreCase(saved.getStatus())) {
                // Process deposit with automatic debt payment
                processDepositWithDebtPayment(saved);
            }

            socketServer.getBroadcastOperations().sendEvent("depositCreated", saved);
            logger.info("Deposit created with id {}", saved.getId());
            return mapToResponse(saved);
        } catch (Exception e) {
            logger.error("Error creating deposit for userId {}", dto.getUserId(), e);
            throw e;
        }
    }

    // ====== READ ALL ======
    public List<DepositResponseDTO> findAll() {
        try {
            logger.info("Fetching all deposits");
            return depositRepository.findAll().stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error fetching all deposits", e);
            throw e;
        }
    }

    // ====== READ BY ID ======
    public DepositResponseDTO findById(Long id) {
        try {
            logger.info("Fetching deposit with id {}", id);
            Deposit deposit = depositRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Deposit not found with id " + id));
            return mapToResponse(deposit);
        } catch (Exception e) {
            logger.error("Error fetching deposit with id {}", id, e);
            throw e;
        }
    }

    // ====== UPDATE ======
    @Transactional
    public DepositResponseDTO update(Long id, DepositRequestDTO dto) {
        try {
            logger.info("Updating deposit with id {}", id);
            Deposit existing = depositRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Deposit not found with id " + id));

            String previousStatus = existing.getStatus();

            existing.setAccountHolder(dto.getAccountHolder());
            existing.setBankName(dto.getBankName());
            existing.setSum(dto.getSum());
            existing.setStatus(dto.getStatus());
            existing.setUserId(dto.getUserId());

            validatePositiveAmount(existing.getSum());
            Deposit saved = depositRepository.save(existing);

            if (!"APPROVED".equalsIgnoreCase(previousStatus) && "APPROVED".equalsIgnoreCase(saved.getStatus())) {
                // Process deposit with automatic debt payment
                processDepositWithDebtPayment(saved);
            }

            socketServer.getBroadcastOperations().sendEvent("depositUpdated", saved);
            logger.info("Deposit updated with id {}", saved.getId());
            return mapToResponse(saved);
        } catch (Exception e) {
            logger.error("Error updating deposit with id {}", id, e);
            throw e;
        }
    }

    // ====== DELETE ======
    public void delete(Long id) {
        try {
            logger.info("Deleting deposit with id {}", id);
            if (!depositRepository.existsById(id)) {
                logger.warn("Deposit not found with id {}", id);
                throw new ResourceNotFoundException("Deposit not found with id " + id);
            }
            depositRepository.deleteById(id);
            socketServer.getBroadcastOperations().sendEvent("depositDeleted", id);
            logger.info("Deposit deleted with id {}", id);
        } catch (Exception e) {
            logger.error("Error deleting deposit with id {}", id, e);
            throw e;
        }
    }

    // ====== MAPPING ======
    private DepositResponseDTO mapToResponse(Deposit deposit) {
        DepositResponseDTO dto = new DepositResponseDTO();
        dto.setId(deposit.getId());
        dto.setAccountHolder(deposit.getAccountHolder());
        dto.setBankName(deposit.getBankName());
        dto.setSum(deposit.getSum());
        dto.setStatus(deposit.getStatus());
        dto.setUserId(deposit.getUserId());
        dto.setCreatedAtDatetime(deposit.getCreatedAtDatetime());
        dto.setUpdatedAtDatetime(deposit.getUpdatedAtDatetime());
        return dto;
    }

    private Deposit mapToEntity(DepositRequestDTO dto) {
        Deposit deposit = new Deposit();
        deposit.setAccountHolder(dto.getAccountHolder());
        deposit.setBankName(dto.getBankName());
        deposit.setSum(dto.getSum());
        deposit.setStatus(dto.getStatus());
        deposit.setUserId(dto.getUserId());
        return deposit;
    }

    private void validatePositiveAmount(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("Deposit amount must be greater than 0");
        }
    }

    private User creditUserBalance(Long userId, BigDecimal amount) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + userId));
        BigDecimal newBalance = user.getBalance() == null ? amount : user.getBalance().add(amount);
        user.setBalance(newBalance);
        return userRepository.save(user);
    }
    
    /**
     * Process deposit with automatic debt payment
     * If user has debts, pay them off first before crediting balance
     */
    private void processDepositWithDebtPayment(Deposit deposit) {
        User user = userRepository.findById(deposit.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + deposit.getUserId()));
        
        // Get all unpaid debts for this user
        List<Debtor> unpaidDebts = debtorRepository.findByUserIdAndPaidFalse(user.getId());
        
        BigDecimal remainingAmount = deposit.getSum();
        BigDecimal totalDebtPaid = BigDecimal.ZERO;
        
        if (!unpaidDebts.isEmpty()) {
            logger.info("User {} has {} unpaid debt(s). Processing automatic payment...", user.getId(), unpaidDebts.size());
            
            for (Debtor debt : unpaidDebts) {
                if (remainingAmount.compareTo(BigDecimal.ZERO) <= 0) {
                    break; // No more money to pay debts
                }
                
                BigDecimal debtAmount = debt.getDebtAmount() != null ? debt.getDebtAmount() : BigDecimal.ZERO;
                
                if (debtAmount.compareTo(BigDecimal.ZERO) <= 0) {
                    // Debt already paid, mark as paid
                    debt.setPaid(true);
                    debtorRepository.save(debt);
                    continue;
                }
                
                if (remainingAmount.compareTo(debtAmount) >= 0) {
                    // Can pay off this debt completely
                    remainingAmount = remainingAmount.subtract(debtAmount);
                    totalDebtPaid = totalDebtPaid.add(debtAmount);
                    
                    debt.setDebtAmount(BigDecimal.ZERO);
                    debt.setPaid(true);
                    debtorRepository.save(debt);
                    
                    // Create transaction record for debt payment
                    transactionService.createDetailedTransaction(
                        user,
                        TransactionType.REFUND, // Using REFUND type for debt payment
                        debtAmount,
                        "COMPLETED",
                        "Debt payment from deposit (fully paid)",
                        debt.getId(),
                        debt.getAccountHolder(),
                        debt.getBankName(),
                        null,
                        "Debt ID: " + debt.getId() + ", from Deposit ID: " + deposit.getId()
                    );
                    
                    logger.info("Debt id={} fully paid: amount={}", debt.getId(), debtAmount);
                } else {
                    // Partial payment
                    BigDecimal partialPayment = remainingAmount;
                    totalDebtPaid = totalDebtPaid.add(partialPayment);
                    
                    debt.setDebtAmount(debtAmount.subtract(partialPayment));
                    debtorRepository.save(debt);
                    
                    // Create transaction record for partial debt payment
                    transactionService.createDetailedTransaction(
                        user,
                        TransactionType.REFUND, // Using REFUND type for debt payment
                        partialPayment,
                        "COMPLETED",
                        "Partial debt payment from deposit",
                        debt.getId(),
                        debt.getAccountHolder(),
                        debt.getBankName(),
                        null,
                        "Debt ID: " + debt.getId() + ", Partial payment: " + partialPayment + ", Remaining debt: " + debt.getDebtAmount()
                    );
                    
                    remainingAmount = BigDecimal.ZERO;
                    logger.info("Debt id={} partially paid: payment={}, remaining={}", 
                               debt.getId(), partialPayment, debt.getDebtAmount());
                    break;
                }
            }
            
            // Update user.isDebtor flag
            boolean hasRemainingDebts = debtorRepository.existsByUserIdAndPaidFalse(user.getId());
            user.setIsDebtor(hasRemainingDebts);
            
            logger.info("Total debt paid: {}, Remaining deposit amount: {}", totalDebtPaid, remainingAmount);
        }
        
        // Credit remaining amount to user balance
        if (remainingAmount.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal newBalance = user.getBalance() == null ? remainingAmount : user.getBalance().add(remainingAmount);
            user.setBalance(newBalance);
            
            // Create transaction record for deposit (only remaining amount)
            transactionService.createDetailedTransaction(
                user,
                TransactionType.DEPOSIT,
                remainingAmount,
                "COMPLETED",
                totalDebtPaid.compareTo(BigDecimal.ZERO) > 0 ? 
                    "Deposit approved (after paying debts: " + totalDebtPaid + ")" : 
                    "Deposit approved and credited to account",
                deposit.getId(),
                deposit.getAccountHolder(),
                deposit.getBankName(),
                null,
                "Deposit ID: " + deposit.getId()
            );
        } else {
            // All deposit used for debt payment
            logger.info("Entire deposit amount used for debt payment. No balance credited.");
            
            // Still create a transaction record showing the deposit was received
            transactionService.createDetailedTransaction(
                user,
                TransactionType.DEPOSIT,
                deposit.getSum(),
                "COMPLETED",
                "Deposit received (fully applied to debt payment: " + totalDebtPaid + ")",
                deposit.getId(),
                deposit.getAccountHolder(),
                deposit.getBankName(),
                null,
                "Deposit ID: " + deposit.getId() + ", All amount used for debt payment"
            );
        }
        
        userRepository.save(user);
        logger.info("Deposit processed: depositAmount={}, debtPaid={}, balanceCredited={}", 
                   deposit.getSum(), totalDebtPaid, remainingAmount);
    }
}

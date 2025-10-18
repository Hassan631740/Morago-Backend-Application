package com.morago_backend.service;

import com.morago_backend.dto.dtoRequest.DebtorRequestDTO;
import com.morago_backend.dto.dtoResponse.DebtorResponseDTO;
import com.morago_backend.entity.Debtor;
import com.morago_backend.entity.User;
import com.morago_backend.exception.ResourceNotFoundException;
import com.morago_backend.repository.DebtorRepository;
import com.morago_backend.repository.UserRepository;
import com.corundumstudio.socketio.SocketIOServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DebtorService {

    private static final Logger logger = LoggerFactory.getLogger(DebtorService.class);

    private final DebtorRepository debtorRepository;
    private final SocketIOServer socketServer;
    private final UserRepository userRepository;

    public DebtorService(DebtorRepository debtorRepository, SocketIOServer socketServer, UserRepository userRepository) {
        this.debtorRepository = debtorRepository;
        this.socketServer = socketServer;
        this.userRepository = userRepository;
    }

    // ====== CREATE ======
    @Transactional
    public DebtorResponseDTO create(DebtorRequestDTO dto) {
        try {
            logger.info("Creating new debtor for userId {}", dto.getUserId());
            Debtor debtor = mapToEntity(dto);
            Debtor saved = debtorRepository.save(debtor);
            
            // Update user isDebtor flag
            updateUserDebtorStatus(saved.getUserId());
            
            socketServer.getBroadcastOperations().sendEvent("debtorCreated", saved);
            logger.info("Debtor created with id {}, user.isDebtor updated", saved.getId());
            return mapToResponse(saved);
        } catch (Exception e) {
            logger.error("Error creating debtor for userId {}", dto.getUserId(), e);
            throw e;
        }
    }

    // ====== READ ALL ======
    public List<DebtorResponseDTO> findAll() {
        try {
            logger.info("Fetching all debtors");
            return debtorRepository.findAll().stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error fetching all debtors", e);
            throw e;
        }
    }

    // ====== READ BY ID ======
    public DebtorResponseDTO findById(Long id) {
        try {
            logger.info("Fetching debtor with id {}", id);
            Debtor debtor = debtorRepository.findById(id)
                    .orElseThrow(() -> {
                        logger.warn("Debtor not found with id {}", id);
                        return new ResourceNotFoundException("Debtor not found with id " + id);
                    });
            return mapToResponse(debtor);
        } catch (Exception e) {
            logger.error("Error fetching debtor with id {}", id, e);
            throw e;
        }
    }

    // ====== UPDATE ======
    @Transactional
    public DebtorResponseDTO update(Long id, DebtorRequestDTO dto) {
        try {
            logger.info("Updating debtor with id {}", id);
            Debtor existing = debtorRepository.findById(id)
                    .orElseThrow(() -> {
                        logger.warn("Debtor not found with id {}", id);
                        return new ResourceNotFoundException("Debtor not found with id " + id);
                    });

            existing.setAccountHolder(dto.getAccountHolder());
            existing.setBankName(dto.getBankName());
            existing.setPaid(dto.getPaid());
            existing.setUserId(dto.getUserId());
            existing.setDebtAmount(dto.getDebtAmount());
            
            // Auto-mark as paid if debt amount is 0 or negative
            if (existing.getDebtAmount() != null && existing.getDebtAmount().signum() <= 0) {
                existing.setPaid(true);
                existing.setDebtAmount(BigDecimal.ZERO);
                logger.info("Debt automatically marked as paid (amount <= 0)");
            }

            Debtor saved = debtorRepository.save(existing);
            
            // Update user isDebtor flag
            updateUserDebtorStatus(saved.getUserId());
            
            socketServer.getBroadcastOperations().sendEvent("debtorUpdated", saved);
            logger.info("Debtor updated with id {}, user.isDebtor updated", saved.getId());
            return mapToResponse(saved);
        } catch (Exception e) {
            logger.error("Error updating debtor with id {}", id, e);
            throw e;
        }
    }

    // ====== DELETE ======
    @Transactional
    public void delete(Long id) {
        try {
            logger.info("Deleting debtor with id {}", id);
            Debtor debtor = debtorRepository.findById(id)
                    .orElseThrow(() -> {
                        logger.warn("Debtor not found with id {}", id);
                        return new ResourceNotFoundException("Debtor not found with id " + id);
                    });
            
            Long userId = debtor.getUserId();
            debtorRepository.deleteById(id);
            
            // Update user isDebtor flag after deletion
            updateUserDebtorStatus(userId);
            
            socketServer.getBroadcastOperations().sendEvent("debtorDeleted", id);
            logger.info("Debtor deleted with id {}, user.isDebtor updated", id);
        } catch (Exception e) {
            logger.error("Error deleting debtor with id {}", id, e);
            throw e;
        }
    }

    // ====== MAPPING ======
    private DebtorResponseDTO mapToResponse(Debtor debtor) {
        DebtorResponseDTO dto = new DebtorResponseDTO();
        dto.setId(debtor.getId());
        dto.setAccountHolder(debtor.getAccountHolder());
        dto.setBankName(debtor.getBankName());
        dto.setPaid(debtor.getPaid());
        dto.setUserId(debtor.getUserId());
        dto.setDebtAmount(debtor.getDebtAmount());
        dto.setCreatedAtDatetime(debtor.getCreatedAtDatetime());
        dto.setUpdatedAtDatetime(debtor.getUpdatedAtDatetime());
        return dto;
    }

    private Debtor mapToEntity(DebtorRequestDTO dto) {
        Debtor debtor = new Debtor();
        debtor.setAccountHolder(dto.getAccountHolder());
        debtor.setBankName(dto.getBankName());
        debtor.setPaid(dto.getPaid());
        debtor.setUserId(dto.getUserId());
        debtor.setDebtAmount(dto.getDebtAmount() != null ? dto.getDebtAmount() : BigDecimal.ZERO);
        return debtor;
    }
    
    // ====== HELPER METHODS ======
    /**
     * Update user.isDebtor flag based on existence of unpaid debts
     */
    private void updateUserDebtorStatus(Long userId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + userId));
            
            // Check if user has any unpaid debts
            boolean hasUnpaidDebts = debtorRepository.existsByUserIdAndPaidFalse(userId);
            
            if (user.getIsDebtor() != hasUnpaidDebts) {
                user.setIsDebtor(hasUnpaidDebts);
                userRepository.save(user);
                logger.info("User {} isDebtor flag updated to: {}", userId, hasUnpaidDebts);
            }
        } catch (Exception e) {
            logger.error("Error updating user debtor status for userId {}", userId, e);
            // Don't throw - this is a secondary operation
        }
    }
}

package com.morago_backend.service;

import com.morago_backend.dto.dtoRequest.CallRecordRequestDTO;
import com.morago_backend.dto.dtoResponse.CallRecordResponseDTO;
import com.morago_backend.entity.CallRecord;
import com.morago_backend.entity.TransactionType;
import com.morago_backend.entity.User;
import com.morago_backend.exception.ResourceNotFoundException;
import com.morago_backend.repository.CallRecordRepository;
import com.morago_backend.repository.UserRepository;
import com.corundumstudio.socketio.SocketIOServer;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CallRecordService {

    private final CallRecordRepository repository;
    private final SocketIOServer socketServer;
    private final UserRepository userRepository;
    private final TransactionService transactionService;
    private static final Logger logger = LoggerFactory.getLogger(CallRecordService.class);

    // ========== CREATE ==========
    public CallRecordResponseDTO create(CallRecordRequestDTO dto) {
        try {
            logger.info("Creating call record with data={}", dto);
            CallRecord entity = mapToEntity(dto);
            CallRecord saved = repository.save(entity);
            socketServer.getBroadcastOperations().sendEvent("callCreated", saved);
            logger.info("Call record created with id={}", saved.getId());
            return mapToResponse(saved);
        } catch (Exception e) {
            logger.error("Error creating call record", e);
            throw e;
        }
    }

    // ========== READ ALL ==========
    public List<CallRecordResponseDTO> findAll() {
        try {
            logger.info("Fetching all call records (sorted by createdAtDatetime desc)");
            return repository.findAll(Sort.by(Sort.Direction.DESC, "createdAtDatetime"))
                    .stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error fetching all call records", e);
            throw e;
        }
    }

    // ========== READ BY ID ==========
    public CallRecordResponseDTO findById(Long id) {
        try {
            logger.info("Fetching call record by id={}", id);
            CallRecord entity = repository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("CallRecord not found with id " + id));
            return mapToResponse(entity);
        } catch (Exception e) {
            logger.error("Error fetching call record id={}", id, e);
            throw e;
        }
    }

    // ========== UPDATE ==========
    @Transactional
    public CallRecordResponseDTO update(Long id, CallRecordRequestDTO dto) {
        try {
            logger.info("Updating call record id={}", id);
            CallRecord existing = repository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("CallRecord not found with id " + id));

            Boolean wasEnded = existing.getEndCall();
            String previousStatusText = existing.getCallStatus();

            if (dto.getDurationSeconds() != null) existing.setDurationSeconds(dto.getDurationSeconds());
            if (dto.getStatus() != null) existing.setStatus(dto.getStatus());
            if (dto.getSum() != null) existing.setSum(dto.getSum());
            if (dto.getCommission() != null) existing.setCommission(dto.getCommission());
            if (dto.getTranslatorHasRated() != null) existing.setTranslatorHasRated(dto.getTranslatorHasRated());
            if (dto.getUserHasRated() != null) existing.setUserHasRated(dto.getUserHasRated());
            if (dto.getCallerUserId() != null) existing.setCallerUserId(dto.getCallerUserId());
            if (dto.getRecipientUserId() != null) existing.setRecipientUserId(dto.getRecipientUserId());
            if (dto.getThemeId() != null) existing.setThemeId(dto.getThemeId());
            if (dto.getChannelName() != null) existing.setChannelName(dto.getChannelName());
            if (dto.getCallStatus() != null) existing.setCallStatus(dto.getCallStatus());
            if (dto.getEndCall() != null) existing.setEndCall(dto.getEndCall());

            boolean transitionsToEnded = (wasEnded == null || !wasEnded) && Boolean.TRUE.equals(existing.getEndCall());
            boolean transitionsToCompleted = (!"COMPLETED".equalsIgnoreCase(previousStatusText))
                    && "COMPLETED".equalsIgnoreCase(existing.getCallStatus());
            if (transitionsToEnded || transitionsToCompleted) {
                settleBalances(existing);
            }

            CallRecord saved = repository.save(existing);
            socketServer.getBroadcastOperations().sendEvent("callUpdated", saved);
            logger.info("Call record updated id={}", saved.getId());
            return mapToResponse(saved);
        } catch (Exception e) {
            logger.error("Error updating call record id={}", id, e);
            throw e;
        }
    }

    // ========== DELETE ==========
    public void delete(Long id) {
        try {
            logger.info("Deleting call record id={}", id);
            repository.deleteById(id);
            socketServer.getBroadcastOperations().sendEvent("callDeleted", id);
            logger.info("Call record deleted id={}", id);
        } catch (Exception e) {
            logger.error("Error deleting call record id={}", id, e);
            throw e;
        }
    }

    // ========== MAPPER ==========
    private CallRecordResponseDTO mapToResponse(CallRecord entity) {
        CallRecordResponseDTO dto = new CallRecordResponseDTO();
        dto.setId(entity.getId());
        dto.setDurationSeconds(entity.getDurationSeconds());
        dto.setStatus(entity.getStatus());
        dto.setSum(entity.getSum());
        dto.setCommission(entity.getCommission());
        dto.setTranslatorHasRated(entity.getTranslatorHasRated());
        dto.setUserHasRated(entity.getUserHasRated());
        dto.setCallerUserId(entity.getCallerUserId());
        dto.setRecipientUserId(entity.getRecipientUserId());
        dto.setThemeId(entity.getThemeId());
        dto.setChannelName(entity.getChannelName());
        dto.setCallStatus(entity.getCallStatus());
        dto.setEndCall(entity.getEndCall());
        dto.setCreatedAtDatetime(entity.getCreatedAtDatetime());
        dto.setUpdatedAtDatetime(entity.getUpdatedAtDatetime());
        return dto;
    }

    private CallRecord mapToEntity(CallRecordRequestDTO dto) {
        CallRecord entity = new CallRecord();
        entity.setDurationSeconds(dto.getDurationSeconds());
        entity.setStatus(dto.getStatus());
        entity.setSum(dto.getSum());
        entity.setCommission(dto.getCommission());
        entity.setTranslatorHasRated(dto.getTranslatorHasRated());
        entity.setUserHasRated(dto.getUserHasRated());
        entity.setCallerUserId(dto.getCallerUserId());
        entity.setRecipientUserId(dto.getRecipientUserId());
        entity.setThemeId(dto.getThemeId());
        entity.setChannelName(dto.getChannelName());
        entity.setCallStatus(dto.getCallStatus());
        entity.setEndCall(dto.getEndCall());
        return entity;
    }

    // ========== BALANCE SETTLEMENT ==========
    private void settleBalances(CallRecord call) {
        try {
            Long callerId = call.getCallerUserId();
            Long interpreterId = call.getRecipientUserId();
            BigDecimal sum = defaultZero(call.getSum());
            BigDecimal commission = defaultZero(call.getCommission());

            if (sum.signum() <= 0) return;
            if (commission.compareTo(sum) > 0) throw new IllegalArgumentException("Commission cannot exceed total sum");

            // Debit caller
            User caller = debitUserBalance(callerId, sum);
            
            // Create transaction record for caller (payment for call)
            transactionService.createDetailedTransaction(
                caller,
                TransactionType.CALL_PAYMENT,
                sum,
                "COMPLETED",
                "Payment for call with interpreter",
                call.getId(),
                null,
                null,
                null,
                "Call ID: " + call.getId() + ", Duration: " + call.getDurationSeconds() + "s"
            );
            logger.info("Transaction record created for caller userId={}, callId={}", callerId, call.getId());
            
            // Credit interpreter (if exists)
            BigDecimal creditAmount = sum.subtract(commission);
            if (interpreterId != null && creditAmount.signum() > 0) {
                User interpreter = creditUserBalance(interpreterId, creditAmount);
                
                // Create transaction record for interpreter (earning from call)
                transactionService.createDetailedTransaction(
                    interpreter,
                    TransactionType.CALL_EARNING,
                    creditAmount,
                    "COMPLETED",
                    "Earning from interpreting call",
                    call.getId(),
                    null,
                    null,
                    null,
                    "Call ID: " + call.getId() + ", Duration: " + call.getDurationSeconds() + "s"
                );
                logger.info("Transaction record created for interpreter userId={}, callId={}", interpreterId, call.getId());
                
                // Create commission transaction record if commission > 0
                if (commission.signum() > 0) {
                    transactionService.createDetailedTransaction(
                        interpreter,
                        TransactionType.COMMISSION,
                        commission,
                        "COMPLETED",
                        "Platform commission deducted",
                        call.getId(),
                        null,
                        null,
                        null,
                        "Call ID: " + call.getId()
                    );
                    logger.info("Commission transaction record created for callId={}", call.getId());
                }
            }
        } catch (Exception e) {
            logger.error("Error settling balances for call id={}", call.getId(), e);
            throw e;
        }
    }

    private BigDecimal defaultZero(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private User debitUserBalance(Long userId, BigDecimal amount) {
        if (userId == null || amount.signum() <= 0) return null;
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + userId));
        BigDecimal current = defaultZero(user.getBalance());
        BigDecimal newBalance = current.subtract(amount);
        if (newBalance.signum() < 0) throw new IllegalStateException("Insufficient balance for user id " + userId);
        user.setBalance(newBalance);
        return userRepository.save(user);
    }

    private User creditUserBalance(Long userId, BigDecimal amount) {
        if (userId == null || amount.signum() <= 0) return null;
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + userId));
        BigDecimal current = defaultZero(user.getBalance());
        BigDecimal newBalance = current.add(amount);
        user.setBalance(newBalance);
        return userRepository.save(user);
    }
}

package com.morago_backend.service;

import com.morago_backend.dto.dtoResponse.PasswordResetResponseDTO;
import com.morago_backend.entity.PasswordReset;
import com.morago_backend.entity.User;
import com.morago_backend.exception.ResourceNotFoundException;
import com.morago_backend.repository.PasswordResetRepository;
import com.morago_backend.repository.UserRepository;
import com.corundumstudio.socketio.SocketIOServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

@Service
public class PasswordResetService {

    private static final Logger logger = LoggerFactory.getLogger(PasswordResetService.class);
    private static final int CODE_EXPIRY_MINUTES = 15;
    private final SecureRandom random = new SecureRandom();

    private final PasswordResetRepository repository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SocketIOServer socketServer;

    public PasswordResetService(PasswordResetRepository repository,
                                UserRepository userRepository,
                                PasswordEncoder passwordEncoder,
                                SocketIOServer socketServer) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.socketServer = socketServer;
    }

    // ========== CREATE RESET REQUEST ==========
    public PasswordResetResponseDTO create(String phone) {
        try {
            logger.info("Creating password reset for phone={}", phone);

            // Check if user exists
            if (!userRepository.findByPhone(phone).isPresent()) {
                logger.warn("Password reset requested for non-existent user: phone={}", phone);
                throw new ResourceNotFoundException("User not found with phone " + phone);
            }

            PasswordReset entity = new PasswordReset();
            entity.setPhone(phone);
            entity.setResetCode(generateSecureCode());
            entity.setIsVerified(false);
            entity.setVerificationToken(generateSecureToken());
            entity.setExpiresAt(LocalDateTime.now().plusMinutes(CODE_EXPIRY_MINUTES));

            PasswordReset saved = repository.save(entity);

            // Send via Socket.IO (you may want to implement SMS sending here)
            socketServer.getRoomOperations(phone).sendEvent("passwordResetCreated", saved);

            PasswordResetResponseDTO dto = toDTO(saved);
            dto.setMessage("Reset code generated and sent to your phone");
            // Don't expose the code in production - this is for testing
            logger.info("Generated reset code for phone={}", phone);
            return dto;
        } catch (Exception e) {
            logger.error("Error creating password reset for phone={}: {}", phone, e.getMessage(), e);
            throw e;
        }
    }

    // Generate secure 4-digit code
    private Integer generateSecureCode() {
        return 1000 + random.nextInt(9000);
    }

    // Generate secure verification token
    private String generateSecureToken() {
        byte[] tokenBytes = new byte[32];
        random.nextBytes(tokenBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
    }

    // ========== VERIFY RESET CODE ==========
    public PasswordResetResponseDTO verifyCode(String phone, Integer code) {
        try {
            PasswordReset reset = repository.findTopByPhoneOrderByCreatedAtDatetimeDesc(phone)
                    .orElseThrow(() -> new ResourceNotFoundException("No reset request found for phone " + phone));

            // Check if code is expired
            if (reset.getExpiresAt() != null && reset.getExpiresAt().isBefore(LocalDateTime.now())) {
                logger.warn("Reset code expired for phone={}", phone);
                throw new RuntimeException("Reset code has expired. Please request a new one.");
            }

            // Check if code matches
            if (!reset.getResetCode().equals(code)) {
                logger.warn("Invalid reset code for phone={}", phone);
                throw new RuntimeException("Invalid reset code");
            }

            // Mark as verified
            reset.setIsVerified(true);
            reset.setVerificationToken(generateSecureToken()); // Generate new token for password update
            repository.save(reset);

            PasswordResetResponseDTO dto = toDTO(reset);
            dto.setMessage("Code verified");
            return dto;
        } catch (Exception e) {
            logger.error("Error verifying reset code for phone={}: {}", phone, e.getMessage(), e);
            throw e;
        }
    }

    // ========== UPDATE PASSWORD ==========
    public PasswordResetResponseDTO updatePassword(String phone, String newPassword, String verificationToken) {
        try {
            // Find the most recent verified reset request for this phone
            PasswordReset reset = repository.findTopByPhoneOrderByCreatedAtDatetimeDesc(phone)
                    .orElseThrow(() -> new ResourceNotFoundException("No reset request found for phone " + phone));

            // Verify that the reset was verified
            if (!Boolean.TRUE.equals(reset.getIsVerified())) {
                logger.warn("Attempt to reset password without verification for phone={}", phone);
                throw new RuntimeException("Please verify your code first");
            }

            // Verify the token matches
            if (!reset.getVerificationToken().equals(verificationToken)) {
                logger.warn("Invalid verification token for phone={}", phone);
                throw new RuntimeException("Invalid verification token");
            }

            // Check if expired
            if (reset.getExpiresAt() != null && reset.getExpiresAt().isBefore(LocalDateTime.now())) {
                logger.warn("Password reset token expired for phone={}", phone);
                throw new RuntimeException("Reset session has expired. Please request a new code.");
            }

            // Validate password
            if (newPassword == null || newPassword.length() < 8) {
                throw new RuntimeException("Password must be at least 8 characters long");
            }

            // Find user by phone
            User user = userRepository.findByPhone(phone)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with phone " + phone));

            // Encode and set new password
            String encoded = passwordEncoder.encode(newPassword);
            user.setPassword(encoded);
            userRepository.save(user);

            // Invalidate the reset request (mark as used)
            repository.delete(reset);

            // Create response DTO
            PasswordResetResponseDTO dto = new PasswordResetResponseDTO();
            dto.setPhone(phone);
            dto.setMessage("Password updated successfully");

            // Optional: send event via Socket.IO if needed
            if (socketServer != null) {
                socketServer.getRoomOperations(phone).sendEvent("passwordUpdated", dto);
            }

            return dto;
        } catch (Exception e) {
            logger.error("Error updating password for phone={}: {}", phone, e.getMessage(), e);
            throw e;
        }
    }

    // ========== MAPPER ==========
    private PasswordResetResponseDTO toDTO(PasswordReset entity) {
        PasswordResetResponseDTO dto = new PasswordResetResponseDTO();
        dto.setId(entity.getId());
        dto.setPhone(entity.getPhone());
        dto.setResetCode(entity.getResetCode());
        dto.setVerificationToken(entity.getVerificationToken());
        dto.setIsVerified(entity.getIsVerified());
        dto.setExpiresAt(entity.getExpiresAt());
        dto.setCreatedAtDatetime(entity.getCreatedAtDatetime());
        dto.setUpdatedAtDatetime(entity.getUpdatedAtDatetime());
        return dto;
    }
}

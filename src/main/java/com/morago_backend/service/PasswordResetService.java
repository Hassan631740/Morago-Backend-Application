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

@Service
public class PasswordResetService {

    private static final Logger logger = LoggerFactory.getLogger(PasswordResetService.class);

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

            PasswordReset entity = new PasswordReset();
            entity.setPhone(phone);
            entity.setResetCode(1234); // hardcoded 4-digit code

            PasswordReset saved = repository.save(entity);

            socketServer.getRoomOperations(phone).sendEvent("passwordResetCreated", saved);

            PasswordResetResponseDTO dto = toDTO(saved);
            dto.setMessage("Reset code generated");
            return dto;
        } catch (Exception e) {
            logger.error("Error creating password reset for phone={}: {}", phone, e.getMessage(), e);
            throw e;
        }
    }

    // ========== VERIFY RESET CODE ==========
    public PasswordResetResponseDTO verifyCode(String phone, Integer code) {
        try {
            PasswordReset reset = repository.findTopByPhoneOrderByCreatedAtDatetimeDesc(phone)
                    .orElseThrow(() -> new ResourceNotFoundException("No reset request found for phone " + phone));

            PasswordResetResponseDTO dto = toDTO(reset);
            if (reset.getResetCode().equals(code)) {
                dto.setMessage("Code verified");
            } else {
                dto.setMessage("Invalid code");
            }
            return dto;
        } catch (Exception e) {
            logger.error("Error verifying reset code for phone={}: {}", phone, e.getMessage(), e);
            throw e;
        }
    }

    // ========== UPDATE PASSWORD ==========
    public PasswordResetResponseDTO updatePasswordMinimal(String phone, String newPassword) {
        try {
            // Find user by phone
            User user = userRepository.findByPhone(phone)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with phone " + phone));

            // Encode and set new password
            String encoded = passwordEncoder.encode(newPassword);
            user.setPassword(encoded);
            userRepository.save(user);

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
        dto.setCreatedAtDatetime(entity.getCreatedAtDatetime());
        dto.setUpdatedAtDatetime(entity.getUpdatedAtDatetime());
        return dto;
    }
}

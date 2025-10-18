package com.morago_backend.controller;

import com.morago_backend.dto.dtoResponse.PasswordResetResponseDTO;
import com.morago_backend.service.PasswordResetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/password-resets")
@PreAuthorize("hasAnyRole('CLIENT','INTERPRETER')")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Password Reset Management - CLIENT/INTERPRETER", description = "APIs for managing password resets")
public class PasswordResetController {

    private static final Logger logger = LoggerFactory.getLogger(PasswordResetController.class);
    private final PasswordResetService service;

    public PasswordResetController(PasswordResetService service) {
        this.service = service;
    }

    // ========== REQUEST RESET ==========
    @Operation(summary = "Request password reset (hardcoded 4-digit code)")
    @PostMapping("/request")
    public ResponseEntity<PasswordResetResponseDTO> requestReset(@RequestParam String phone) {
        try {
            PasswordResetResponseDTO dto = service.create(phone);
            return ResponseEntity.status(201).body(dto);
        } catch (Exception ex) {
            logger.error("Error requesting password reset for phone={}: {}", phone, ex.getMessage(), ex);
            return ResponseEntity.status(500).build();
        }
    }

    // ========== VERIFY RESET CODE ==========
    @Operation(summary = "Verify password reset code")
    @PostMapping("/verify")
    public ResponseEntity<PasswordResetResponseDTO> verifyCode(@RequestParam String phone,
                                                               @RequestParam Integer code) {
        try {
            PasswordResetResponseDTO dto = service.verifyCode(phone, code);
            return ResponseEntity.ok(dto);
        } catch (Exception ex) {
            logger.error("Error verifying reset code for phone={}: {}", phone, ex.getMessage(), ex);
            return ResponseEntity.status(500).build();
        }
    }

    // ========== UPDATE PASSWORD ==========
    @Operation(summary = "Update password after code verification")
    @PostMapping("/update-password")
    public ResponseEntity<PasswordResetResponseDTO> updatePassword(
            @RequestParam String phone,
            @RequestParam String newPassword) {

        // Validate request parameters
        if (phone == null || phone.isBlank() || newPassword == null || newPassword.isBlank()) {
            logger.warn("Invalid request: phone or newPassword is empty");
            return ResponseEntity.badRequest().build(); // 400 BAD_REQUEST
        }

        try {
            // Call service to update password
            PasswordResetResponseDTO dto = service.updatePasswordMinimal(phone, newPassword);

            // Return 200 OK with DTO
            return ResponseEntity.ok(dto);

        } catch (Exception ex) {
            // Log full stack trace for debugging
            logger.error("Error updating password for phone={}: {}", phone, ex.getMessage(), ex);
            return ResponseEntity.status(500).build(); // 500 INTERNAL_SERVER_ERROR
        }
    }
}

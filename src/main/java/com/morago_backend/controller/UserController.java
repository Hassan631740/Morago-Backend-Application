package com.morago_backend.controller;

import com.morago_backend.dto.dtoRequest.PasswordChangeRequestDTO;
import com.morago_backend.dto.dtoRequest.UserRequestDTO;
import com.morago_backend.dto.dtoResponse.UserResponseDTO;
import com.morago_backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/users")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "User Management - ADMIN/CLIENT(deposit)", description = "APIs for user profile, deposit, and withdraw")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    //=== Get current user profile ===//
    @Operation(summary = "Get current user profile")
    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getProfile() {
        try {
            logger.info("Fetching current user profile");
            return ResponseEntity.ok(userService.getCurrentUserProfile());
        } catch (Exception e) {
            logger.error("Error fetching current user profile", e);
            return ResponseEntity.status(500).build();
        }
    }

    //=== Update current user profile ===//
    @Operation(summary = "Update current user profile")
    @PutMapping("/me")
    public ResponseEntity<UserResponseDTO> updateProfile(@Valid @RequestBody UserRequestDTO request) {
        try {
            logger.info("Updating current user profile");
            return ResponseEntity.ok(userService.updateCurrentUser(request));
        } catch (Exception e) {
            logger.error("Error updating current user profile", e);
            return ResponseEntity.status(500).build();
        }
    }

    //=== Change password (for authenticated users) ===//
    @Operation(summary = "Change password for authenticated users")
    @PostMapping("/password")
    public ResponseEntity<String> changePassword(@Valid @RequestBody PasswordChangeRequestDTO request) {
        try {
            logger.info("Changing password for authenticated user");
            userService.changePassword(request);
            return ResponseEntity.ok("Password changed successfully");
        } catch (Exception e) {
            logger.error("Error changing password", e);
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    //=== Deposit money (Client only) ===//
    @Operation(summary = "Deposit money into current user's account")
    @PreAuthorize("hasRole('CLIENT')") //=== only clients can deposit ===//
    @PostMapping("/deposit")
    public ResponseEntity<UserResponseDTO> deposit(@RequestParam BigDecimal amount) {
        try {
            logger.info("Depositing amount={}", amount);
            return ResponseEntity.ok(userService.deposit(amount));
        } catch (Exception e) {
            logger.error("Error depositing money", e);
            return ResponseEntity.status(500).build();
        }
    }

    //=== Get current balance (all users) ===//
    @Operation(summary = "Get current user's account balance")
    @GetMapping("/balance")
    public ResponseEntity<BigDecimal> getBalance() {
        try {
            logger.info("Fetching current user balance");
            return ResponseEntity.ok(userService.getBalance());
        } catch (Exception e) {
            logger.error("Error fetching user balance", e);
            return ResponseEntity.status(500).build();
        }
    }
}

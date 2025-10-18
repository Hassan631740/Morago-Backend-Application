package com.morago_backend.controller;

import com.morago_backend.dto.dtoResponse.UserRoleResponseDTO;
import com.morago_backend.service.UserRoleManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/user-roles")
@PreAuthorize("hasRole('ADMINISTRATOR')")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "User Role Management - ADMIN", description = "Endpoints to manage user roles")
public class UserRoleManagementController {

    private final UserRoleManagementService userRoleManagementService;
    private static final Logger logger = LoggerFactory.getLogger(UserRoleManagementController.class);

    public UserRoleManagementController(UserRoleManagementService userRoleManagementService) {
        this.userRoleManagementService = userRoleManagementService;
    }

    // ========== GET USER ROLES ==========
    @Operation(summary = "Get user roles")
    @GetMapping("/{userId}/roles")
    public ResponseEntity<Set<String>> getUserRoles(@PathVariable Long userId) {
        try {
            logger.info("Fetching roles for user id={}", userId);
            Set<String> roles = userRoleManagementService.getUserRoles(userId);
            return ResponseEntity.ok(roles);
        } catch (Exception ex) {
            logger.error("Error fetching roles for user id={}: {}", userId, ex.getMessage(), ex);
            return ResponseEntity.internalServerError().build();
        }
    }

    // ========== CHECK IF USER HAS ROLE ==========
    @Operation(summary = "Check if user has role")
    @GetMapping("/{userId}/has-role/{roleName}")
    public ResponseEntity<Boolean> userHasRole(@PathVariable Long userId, @PathVariable String roleName) {
        try {
            logger.info("Checking if user id={} has role {}", userId, roleName);
            Boolean hasRole = userRoleManagementService.userHasRole(userId, roleName);
            return ResponseEntity.ok(hasRole);
        } catch (Exception ex) {
            logger.error("Error checking role {} for user id={}: {}", roleName, userId, ex.getMessage(), ex);
            return ResponseEntity.internalServerError().build();
        }
    }

    // ========== GET USERS BY ROLE ==========
    @Operation(summary = "Get users by role")
    @GetMapping("/by-role/{roleName}")
    public ResponseEntity<List<UserRoleResponseDTO>> getUsersByRole(@PathVariable String roleName) {
        try {
            logger.info("Fetching users with role {}", roleName);
            List<UserRoleResponseDTO> users = userRoleManagementService.getUsersByRole(roleName);
            return ResponseEntity.ok(users);
        } catch (Exception ex) {
            logger.error("Error fetching users by role {}: {}", roleName, ex.getMessage(), ex);
            return ResponseEntity.internalServerError().build();
        }
    }

    // ========== GET ALL CLIENTS ==========
    @GetMapping("/clients")
    public ResponseEntity<List<UserRoleResponseDTO>> getAllClients() {
        try {
            logger.info("Fetching all clients");
            return ResponseEntity.ok(userRoleManagementService.getAllClients());
        } catch (Exception ex) {
            logger.error("Error fetching all clients: {}", ex.getMessage(), ex);
            return ResponseEntity.internalServerError().build();
        }
    }

    // ========== GET ALL INTERPRETERS ==========
    @GetMapping("/interpreters")
    public ResponseEntity<List<UserRoleResponseDTO>> getAllInterpreters() {
        try {
            logger.info("Fetching all interpreters");
            return ResponseEntity.ok(userRoleManagementService.getAllInterpreters());
        } catch (Exception ex) {
            logger.error("Error fetching all interpreters: {}", ex.getMessage(), ex);
            return ResponseEntity.internalServerError().build();
        }
    }
}

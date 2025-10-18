package com.morago_backend.controller;

import com.morago_backend.dto.dtoRequest.FilterRequest;
import com.morago_backend.dto.dtoRequest.PaginationRequest;
import com.morago_backend.dto.dtoRequest.UserRequestDTO;
import com.morago_backend.dto.dtoResponse.PagedResponse;
import com.morago_backend.dto.dtoResponse.UserResponseDTO;
import com.morago_backend.exception.ResourceNotFoundException;
import com.morago_backend.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMINISTRATOR')")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Admin Management - ADMIN", description = "Admin-only endpoints")
public class AdminController {

    private final AdminService service;
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    public AdminController(AdminService service) {
        this.service = service;
    }

    // ========== GET ALL USERS ==========
    @Operation(summary = "Get all users with pagination and filtering", tags = {"Admin - Administrator"})
    @GetMapping("/users")
    public ResponseEntity<PagedResponse<UserResponseDTO>> getAll(
            @Parameter(description = "Pagination parameters") @Valid PaginationRequest pagination,
            @Parameter(description = "Filter parameters") FilterRequest filter) {
        try {
            logger.info("Fetching all users with pagination={} filter={}", pagination, filter);
            PagedResponse<UserResponseDTO> response = service.findAllDTOWithPaginationAndFilter(pagination, filter);
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            logger.error("Error fetching users: {}", ex.getMessage(), ex);
            return ResponseEntity.status(500).build();
        }
    }

    // ========== GET USER BY ID ==========
    @Operation(summary = "Get user by ID", tags = {"Admin - Administrator"})
    @GetMapping("/users/{id}")
    public ResponseEntity<UserResponseDTO> getById(@PathVariable Long id) {
        try {
            logger.info("Fetching user by id={}", id);
            UserResponseDTO dto = service.findByIdDTO(id)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));
            return ResponseEntity.ok(dto);
        } catch (ResourceNotFoundException ex) {
            logger.warn("User not found id={}", id);
            throw ex;
        } catch (Exception ex) {
            logger.error("Error fetching user id={}: {}", id, ex.getMessage(), ex);
            return ResponseEntity.status(500).build();
        }
    }

    // ========== CREATE USER ==========
    @Operation(summary = "Create new user", tags = {"Admin - Administrator"})
    @PostMapping("/users")
    public ResponseEntity<UserResponseDTO> create(@Valid @RequestBody UserRequestDTO request) {
        try {
            logger.info("Creating new user with data={}", request);
            UserResponseDTO created = service.create(request);
            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(created.getId())
                    .toUri();
            return ResponseEntity.created(location).body(created);
        } catch (Exception ex) {
            logger.error("Error creating user: {}", ex.getMessage(), ex);
            return ResponseEntity.status(500).build();
        }
    }

    // ========== UPDATE USER ==========
    @Operation(summary = "Update user by ID", tags = {"Admin - Administrator"})
    @PutMapping("/users/{id}")
    public ResponseEntity<UserResponseDTO> update(@PathVariable Long id, @Valid @RequestBody UserRequestDTO request) {
        try {
            logger.info("Updating user id={} with data={}", id, request);
            UserResponseDTO updated = service.update(id, request);
            return ResponseEntity.ok(updated);
        } catch (Exception ex) {
            logger.error("Error updating user id={}: {}", id, ex.getMessage(), ex);
            return ResponseEntity.status(500).build();
        }
    }

    // ========== DELETE USER ==========
    @Operation(summary = "Delete user by ID", tags = {"Admin - Administrator"})
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            logger.info("Deleting user id={}", id);
            service.delete(id);
            return ResponseEntity.noContent().build();
        } catch (Exception ex) {
            logger.error("Error deleting user id={}: {}", id, ex.getMessage(), ex);
            return ResponseEntity.status(500).build();
        }
    }

    // ========== BLOCK USER ==========
    @Operation(summary = "Block user by ID", tags = {"Admin - Administrator"})
    @PostMapping("/users/{id}/block")
    public ResponseEntity<UserResponseDTO> blockUser(@PathVariable Long id) {
        try {
            logger.info("Blocking user id={}", id);
            UserResponseDTO dto = service.blockUser(id);
            return ResponseEntity.ok(dto);
        } catch (Exception ex) {
            logger.error("Error blocking user id={}: {}", id, ex.getMessage(), ex);
            return ResponseEntity.status(500).build();
        }
    }

    // ========== ACTIVATE USER ==========
    @Operation(summary = "Activate user by ID", tags = {"Admin - Administrator"})
    @PostMapping("/users/{id}/activate")
    public ResponseEntity<UserResponseDTO> activateUser(@PathVariable Long id) {
        try {
            logger.info("Activating user id={}", id);
            UserResponseDTO dto = service.activateUser(id);
            return ResponseEntity.ok(dto);
        } catch (Exception ex) {
            logger.error("Error activating user id={}: {}", id, ex.getMessage(), ex);
            return ResponseEntity.status(500).build();
        }
    }
}

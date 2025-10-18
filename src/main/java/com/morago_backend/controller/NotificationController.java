package com.morago_backend.controller;

import com.morago_backend.dto.dtoRequest.NotificationRequestDTO;
import com.morago_backend.dto.dtoResponse.NotificationResponseDTO;
import com.morago_backend.exception.ResourceNotFoundException;
import com.morago_backend.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@PreAuthorize("hasAnyRole('CLIENT','INTERPRETER','ADMINISTRATOR')")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Notification Management - All roles", description = "APIs for managing notifications")
public class NotificationController {

    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);
    private final NotificationService service;

    public NotificationController(NotificationService service) {
        this.service = service;
    }

    // ========== CREATE NEW NOTIFICATION ==========
    @Operation(summary = "Create new notification")
    @PostMapping
    public ResponseEntity<NotificationResponseDTO> create(
            @Valid @RequestBody NotificationRequestDTO dto,
            UriComponentsBuilder uriBuilder
    ) {
        try {
            logger.info("Creating new notification with data={}", dto);
            NotificationResponseDTO created = service.create(dto);
            URI location = uriBuilder.path("/api/notifications/{id}")
                    .buildAndExpand(created.getId()).toUri();
            return ResponseEntity.created(location).body(created);
        } catch (Exception ex) {
            logger.error("Error creating notification: {}", ex.getMessage(), ex);
            return ResponseEntity.status(500).build();
        }
    }

    // ========== GET ALL NOTIFICATIONS ==========
    @Operation(summary = "Get all notifications")
    @GetMapping
    public ResponseEntity<List<NotificationResponseDTO>> findAll() {
        try {
            logger.info("Fetching all notifications");
            List<NotificationResponseDTO> list = service.findAll();
            return ResponseEntity.ok(list);
        } catch (Exception ex) {
            logger.error("Error fetching notifications: {}", ex.getMessage(), ex);
            return ResponseEntity.status(500).build();
        }
    }

    // ========== GET NOTIFICATION BY ID ==========
    @Operation(summary = "Get notification by ID")
    @GetMapping("/{id}")
    public ResponseEntity<NotificationResponseDTO> findById(@PathVariable Long id) {
        try {
            logger.info("Fetching notification by id={}", id);
            NotificationResponseDTO response = service.findById(id);
            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            logger.error("Error fetching notification id={}: {}", id, ex.getMessage(), ex);
            return ResponseEntity.status(500).build();
        }
    }

    // ========== UPDATE NOTIFICATION ==========
    @Operation(summary = "Update notification by ID")
    @PutMapping("/{id}")
    public ResponseEntity<NotificationResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody NotificationRequestDTO dto
    ) {
        try {
            logger.info("Updating notification id={} with data={}", id, dto);
            NotificationResponseDTO updated = service.update(id, dto);
            return ResponseEntity.ok(updated);
        } catch (Exception ex) {
            logger.error("Error updating notification id={}: {}", id, ex.getMessage(), ex);
            return ResponseEntity.status(500).build();
        }
    }

    // ========== DELETE NOTIFICATION ==========
    @Operation(summary = "Delete notification by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            logger.info("Deleting notification id={}", id);
            service.delete(id);
            return ResponseEntity.noContent().build();
        } catch (Exception ex) {
            logger.error("Error deleting notification id={}: {}", id, ex.getMessage(), ex);
            return ResponseEntity.status(500).build();
        }
    }
}

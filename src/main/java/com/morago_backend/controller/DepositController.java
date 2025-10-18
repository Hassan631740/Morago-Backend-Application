package com.morago_backend.controller;

import com.morago_backend.dto.dtoRequest.DepositRequestDTO;
import com.morago_backend.dto.dtoResponse.DepositResponseDTO;
import com.morago_backend.service.DepositService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/deposits")
@PreAuthorize("hasRole('ADMINISTRATOR')")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Deposit Management - ADMIN")
public class DepositController {

    private static final Logger logger = LoggerFactory.getLogger(DepositController.class);
    private final DepositService depositService;

    public DepositController(DepositService depositService) {
        this.depositService = depositService;
    }

    // ========== GET ALL DEPOSITS ==========
    @Operation(summary = "Get all deposits")
    @GetMapping
    public ResponseEntity<List<DepositResponseDTO>> getAll() {
        try {
            logger.info("GET /api/deposits called");
            return ResponseEntity.ok(depositService.findAll());
        } catch (Exception e) {
            logger.error("Error fetching deposits: {}", e.getMessage());
            throw e;
        }
    }

    // ========== GET DEPOSIT BY ID ==========
    @Operation(summary = "Get deposit by ID")
    @GetMapping("/{id}")
    public ResponseEntity<DepositResponseDTO> getById(@PathVariable Long id) {
        try {
            logger.info("GET /api/deposits/{} called", id);
            return ResponseEntity.ok(depositService.findById(id));
        } catch (Exception e) {
            logger.error("Error fetching deposit id={}: {}", id, e.getMessage());
            throw e;
        }
    }

    // ========== CREATE NEW DEPOSIT ==========
    @Operation(summary = "Create new deposit")
    @PostMapping
    public ResponseEntity<DepositResponseDTO> create(@Valid @RequestBody DepositRequestDTO dto) {
        try {
            logger.info("POST /api/deposits called");
            DepositResponseDTO created = depositService.create(dto);
            return ResponseEntity.created(URI.create("/api/deposits/" + created.getId()))
                    .body(created);
        } catch (Exception e) {
            logger.error("Error creating deposit: {}", e.getMessage());
            throw e;
        }
    }

    // ========== UPDATE DEPOSIT ==========
    @Operation(summary = "Update deposit by ID")
    @PutMapping("/{id}")
    public ResponseEntity<DepositResponseDTO> update(@PathVariable Long id, @Valid @RequestBody DepositRequestDTO dto) {
        try {
            logger.info("PUT /api/deposits/{} called", id);
            return ResponseEntity.ok(depositService.update(id, dto));
        } catch (Exception e) {
            logger.error("Error updating deposit id={}: {}", id, e.getMessage());
            throw e;
        }
    }

    // ========== DELETE DEPOSIT ==========
    @Operation(summary = "Delete deposit by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            logger.info("DELETE /api/deposits/{} called", id);
            depositService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("Error deleting deposit id={}: {}", id, e.getMessage());
            throw e;
        }
    }
}

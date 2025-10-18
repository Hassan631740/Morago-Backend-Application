package com.morago_backend.controller;

import com.morago_backend.dto.dtoRequest.DebtorRequestDTO;
import com.morago_backend.dto.dtoResponse.DebtorResponseDTO;
import com.morago_backend.service.DebtorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/debtors")
@PreAuthorize("hasRole('ADMINISTRATOR')")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Debtor Management - ADMIN")
public class DebtorController {

    private static final Logger logger = LoggerFactory.getLogger(DebtorController.class);
    private final DebtorService debtorService;

    public DebtorController(DebtorService debtorService) {
        this.debtorService = debtorService;
    }

    // ========== GET ALL DEBTORS ==========
    @Operation(summary = "Get all debtors")
    @GetMapping
    public ResponseEntity<List<DebtorResponseDTO>> getAll() {
        try {
            logger.info("GET /api/debtors called");
            return ResponseEntity.ok(debtorService.findAll());
        } catch (Exception e) {
            logger.error("Error fetching all debtors: {}", e.getMessage());
            throw e;
        }
    }

    // ========== GET DEBTOR BY ID ==========
    @Operation(summary = "Get debtor by ID")
    @GetMapping("/{id}")
    public ResponseEntity<DebtorResponseDTO> getById(@PathVariable Long id) {
        try {
            logger.info("GET /api/debtors/{} called", id);
            return ResponseEntity.ok(debtorService.findById(id));
        } catch (Exception e) {
            logger.error("Error fetching debtor id={}: {}", id, e.getMessage());
            throw e;
        }
    }

    // ========== CREATE NEW DEBTOR ==========
    @Operation(summary = "Create new debtor")
    @PostMapping
    public ResponseEntity<DebtorResponseDTO> create(@Valid @RequestBody DebtorRequestDTO dto) {
        try {
            logger.info("POST /api/debtors called for userId {}", dto.getUserId());
            DebtorResponseDTO created = debtorService.create(dto);
            return ResponseEntity.created(URI.create("/api/debtors/" + created.getId()))
                    .body(created);
        } catch (Exception e) {
            logger.error("Error creating debtor for userId {}: {}", dto.getUserId(), e.getMessage());
            throw e;
        }
    }

    // ========== UPDATE DEBTOR ==========
    @Operation(summary = "Update debtor by ID")
    @PutMapping("/{id}")
    public ResponseEntity<DebtorResponseDTO> update(@PathVariable Long id, @Valid @RequestBody DebtorRequestDTO dto) {
        try {
            logger.info("PUT /api/debtors/{} called", id);
            return ResponseEntity.ok(debtorService.update(id, dto));
        } catch (Exception e) {
            logger.error("Error updating debtor id={}: {}", id, e.getMessage());
            throw e;
        }
    }

    // ========== DELETE DEBTOR ==========
    @Operation(summary = "Delete debtor by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            logger.info("DELETE /api/debtors/{} called", id);
            debtorService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("Error deleting debtor id={}: {}", id, e.getMessage());
            throw e;
        }
    }
}

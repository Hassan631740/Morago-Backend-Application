package com.morago_backend.controller;

import com.morago_backend.dto.dtoRequest.ThemeRequestDTO;
import com.morago_backend.dto.dtoResponse.ThemeResponseDTO;
import com.morago_backend.exception.ResourceNotFoundException;
import com.morago_backend.service.ThemeService;
import io.swagger.v3.oas.annotations.Operation;
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
import java.util.List;

@RestController
@RequestMapping("/api/themes")
@PreAuthorize("hasRole('ADMINISTRATOR')")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Theme Management - ADMIN")
public class ThemeController {

    private final ThemeService service;
    private static final Logger logger = LoggerFactory.getLogger(ThemeController.class);

    public ThemeController(ThemeService service) {
        this.service = service;
    }

    // ========== GET ALL THEMES ==========
    @Operation(summary = "Get all themes")
    @GetMapping
    public ResponseEntity<List<ThemeResponseDTO>> getAll() {
        try {
            logger.info("Fetching all themes");
            List<ThemeResponseDTO> list = service.findAll();
            return ResponseEntity.ok(list);
        } catch (Exception ex) {
            logger.error("Error fetching all themes: {}", ex.getMessage(), ex);
            return ResponseEntity.status(500).build();
        }
    }

    // ========== GET THEME BY ID ==========
    @Operation(summary = "Get theme by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ThemeResponseDTO> getById(@PathVariable Long id) {
        try {
            logger.info("Fetching theme by id={}", id);
            ThemeResponseDTO dto = service.findById(id); // langsung panggil
            return ResponseEntity.ok(dto);
        } catch (ResourceNotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            logger.error("Error fetching theme id={}: {}", id, ex.getMessage(), ex);
            return ResponseEntity.status(500).build();
        }
    }

    // ========== CREATE NEW THEME ==========
    @Operation(summary = "Create new theme")
    @PostMapping
    public ResponseEntity<ThemeResponseDTO> create(@Valid @RequestBody ThemeRequestDTO request) {
        try {
            logger.info("Creating new theme with data={}", request);
            ThemeResponseDTO created = service.create(request);
            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(created.getId())
                    .toUri();
            return ResponseEntity.created(location).body(created);
        } catch (Exception ex) {
            logger.error("Error creating theme: {}", ex.getMessage(), ex);
            return ResponseEntity.status(500).build();
        }
    }

    // ========== UPDATE THEME BY ID ==========
    @Operation(summary = "Update theme by ID")
    @PutMapping("/{id}")
    public ResponseEntity<ThemeResponseDTO> update(@PathVariable Long id,
                                                   @Valid @RequestBody ThemeRequestDTO request) {
        try {
            logger.info("Updating theme id={}", id);
            ThemeResponseDTO updated = service.update(id, request);
            return ResponseEntity.ok(updated);
        } catch (Exception ex) {
            logger.error("Error updating theme id={}: {}", id, ex.getMessage(), ex);
            return ResponseEntity.status(500).build();
        }
    }

    // ========== DELETE THEME BY ID ==========
    @Operation(summary = "Delete theme by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            logger.info("Deleting theme id={}", id);
            service.delete(id);
            return ResponseEntity.noContent().build();
        } catch (Exception ex) {
            logger.error("Error deleting theme id={}: {}", id, ex.getMessage(), ex);
            return ResponseEntity.status(500).build();
        }
    }
}

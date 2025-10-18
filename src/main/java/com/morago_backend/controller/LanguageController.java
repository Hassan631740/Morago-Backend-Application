package com.morago_backend.controller;

import com.morago_backend.dto.dtoRequest.FilterRequest;
import com.morago_backend.dto.dtoRequest.PaginationRequest;
import com.morago_backend.dto.dtoRequest.LanguageRequestDTO;
import com.morago_backend.dto.dtoResponse.LanguageResponseDTO;
import com.morago_backend.dto.dtoResponse.PagedResponse;
import com.morago_backend.service.LanguageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/languages")
@PreAuthorize("hasRole('ADMINISTRATOR')")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Language Management - ADMIN")
public class LanguageController {

    private final LanguageService service;
    private static final Logger logger = LoggerFactory.getLogger(LanguageController.class);

    public LanguageController(LanguageService service) {
        this.service = service;
    }

    // ================= GET ALL LANGUAGES WITH PAGINATION =================
    @Operation(summary = "Get all languages with pagination and optional filtering")
    @GetMapping
    public ResponseEntity<PagedResponse<LanguageResponseDTO>> getAll(
            @Valid PaginationRequest pagination,
            FilterRequest filter
    ) {
        try {
            logger.info("Fetching languages with pagination={} and filter={}", pagination, filter);
            PagedResponse<LanguageResponseDTO> response = service.findAll(pagination, filter);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error fetching languages: {}", e.getMessage());
            throw e;
        }
    }

    // ================= GET ALL LANGUAGES WITHOUT PAGINATION =================
    @Operation(summary = "Get all languages (legacy endpoint without pagination)")
    @GetMapping("/all")
    public ResponseEntity<List<LanguageResponseDTO>> getAllLegacy() {
        try {
            logger.info("Fetching all languages (legacy endpoint)");
            List<LanguageResponseDTO> list = service.findAll();
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            logger.error("Error fetching all languages: {}", e.getMessage());
            throw e;
        }
    }

    // ================= GET LANGUAGE BY ID =================
    @Operation(summary = "Get language by ID")
    @GetMapping("/{id}")
    public ResponseEntity<LanguageResponseDTO> getById(@PathVariable Long id) {
        try {
            logger.info("Fetching language by id={}", id);
            LanguageResponseDTO dto = service.findById(id);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            logger.error("Error fetching language id={}: {}", id, e.getMessage());
            throw e;
        }
    }

    // ================= CREATE LANGUAGE =================
    @Operation(summary = "Create a new language")
    @PostMapping
    public ResponseEntity<LanguageResponseDTO> create(@Valid @RequestBody LanguageRequestDTO dto) {
        try {
            logger.info("Creating new language with data={}", dto);
            LanguageResponseDTO created = service.create(dto);
            return ResponseEntity.status(201).body(created);
        } catch (Exception e) {
            logger.error("Error creating language: {}", e.getMessage());
            throw e;
        }
    }

    // ================= UPDATE LANGUAGE =================
    @Operation(summary = "Update language by ID")
    @PutMapping("/{id}")
    public ResponseEntity<LanguageResponseDTO> update(@PathVariable Long id,
                                                      @Valid @RequestBody LanguageRequestDTO dto) {
        try {
            logger.info("Updating language id={}", id);
            LanguageResponseDTO updated = service.update(id, dto);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            logger.error("Error updating language id={}: {}", id, e.getMessage());
            throw e;
        }
    }

    // ================= DELETE LANGUAGE =================
    @Operation(summary = "Delete language by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            logger.info("Deleting language id={}", id);
            service.delete(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("Error deleting language id={}: {}", id, e.getMessage());
            throw e;
        }
    }
}

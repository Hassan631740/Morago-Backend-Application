package com.morago_backend.controller;

import com.morago_backend.dto.dtoRequest.FileRequestDTO;
import com.morago_backend.dto.dtoResponse.FileResponseDTO;
import com.morago_backend.exception.ResourceNotFoundException;
import com.morago_backend.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/files")
@Tag(name = "File Management - All Roles", description = "Operations related to file management")
@SecurityRequirement(name = "bearerAuth")
public class FileController {

    private static final Logger logger = LoggerFactory.getLogger(FileController.class);
    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    // ========== GET ALL FILES ==========
    @Operation(summary = "Get all files")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRATOR','CLIENT')")
    public ResponseEntity<List<FileResponseDTO>> getAll() {
        try {
            logger.info("Fetching all files");
            return ResponseEntity.ok(fileService.findAll());
        } catch (Exception e) {
            logger.error("Error fetching files: {}", e.getMessage());
            throw e;
        }
    }

    // ========== GET FILE BY ID ==========
    @Operation(summary = "Get file by ID")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR','CLIENT')")
    public ResponseEntity<FileResponseDTO> getById(@PathVariable Long id) {
        try {
            logger.info("Fetching file by id={}", id);
            FileResponseDTO dto = fileService.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("File not found with id " + id));
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            logger.error("Error fetching file id={}: {}", id, e.getMessage());
            throw e;
        }
    }

    // ========== CREATE NEW FILE ==========
    @Operation(summary = "Create a new file")
    @PostMapping(consumes = {"multipart/form-data"})
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<FileResponseDTO> create(
            @Valid @ModelAttribute FileRequestDTO dto,
            @RequestPart(required = false) MultipartFile file) {
        try {
            logger.info("Creating new file");
            if (file != null) {
                dto.setFile(file); // pass the uploaded file to DTO
            }
            FileResponseDTO created = fileService.create(dto);
            return ResponseEntity.created(URI.create("/api/files/" + created.getId()))
                    .body(created);
        } catch (Exception e) {
            logger.error("Error creating file: {}", e.getMessage());
            throw e;
        }
    }

    // ========== UPDATE FILE ==========
    @Operation(summary = "Update a file")
    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<FileResponseDTO> update(
            @PathVariable Long id,
            @Valid @ModelAttribute FileRequestDTO dto,
            @RequestPart(required = false) MultipartFile file) {
        try {
            logger.info("Updating file id={}", id);
            if (file != null) {
                dto.setFile(file); // pass the uploaded file to DTO
            }
            FileResponseDTO updated = fileService.update(id, dto);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            logger.error("Error updating file id={}: {}", id, e.getMessage());
            throw e;
        }
    }

    // ========== DELETE FILE ==========
    @Operation(summary = "Delete a file")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            logger.info("Deleting file id={}", id);
            fileService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("Error deleting file id={}: {}", id, e.getMessage());
            throw e;
        }
    }
}

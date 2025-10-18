package com.morago_backend.controller;

import com.morago_backend.dto.dtoResponse.FileResourceResponseDTO;
import com.morago_backend.dto.dtoRequest.FileResourceRequestDTO;
import com.morago_backend.entity.FileResource;
import com.morago_backend.service.FileResourceService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/file-resources")
@PreAuthorize("hasAnyRole('CLIENT','INTERPRETER','ADMINISTRATOR')")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "File Resource Management - All roles")
public class FileResourceController {

    private final FileResourceService fileResourceService;
    private static final Logger logger = LoggerFactory.getLogger(FileResourceController.class);

    public FileResourceController(FileResourceService fileResourceService) {
        this.fileResourceService = fileResourceService;
    }

    // ================= Helper Mapper =================
    private FileResourceResponseDTO mapToDTO(FileResource entity) {
        FileResourceResponseDTO dto = new FileResourceResponseDTO();
        dto.setId(entity.getId());
        dto.setOriginType(entity.getOriginType());
        dto.setPath(entity.getPath());
        dto.setType(entity.getType());
        dto.setCreatedAtDatetime(entity.getCreatedAtDatetime());
        dto.setUpdatedAtDatetime(entity.getUpdatedAtDatetime());
        return dto;
    }

    // ================= GET ALL FILE RESOURCES =================
    @Operation(summary = "Get all file resources")
    @GetMapping
    public ResponseEntity<List<FileResourceResponseDTO>> getAll() {
        try {
            logger.info("Fetching all file resources");
            List<FileResourceResponseDTO> list = fileResourceService.findAll().stream()
                    .map(this::mapToDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            logger.error("Error fetching all file resources: {}", e.getMessage());
            throw e;
        }
    }

    // ================= GET FILE RESOURCE BY ID =================
    @Operation(summary = "Get file resource by ID")
    @GetMapping("/{id}")
    public ResponseEntity<FileResourceResponseDTO> getById(@PathVariable Long id) {
        try {
            logger.info("Fetching file resource by id={}", id);
            FileResource entity = fileResourceService.findById(id)
                    .orElseThrow(() -> new RuntimeException("FileResource not found with id " + id));
            return ResponseEntity.ok(mapToDTO(entity));
        } catch (Exception e) {
            logger.error("Error fetching file resource id={}: {}", id, e.getMessage());
            throw e;
        }
    }

    // ================= CREATE FILE RESOURCE =================
    @Operation(summary = "Create new file resource")
    @PostMapping
    public ResponseEntity<FileResourceResponseDTO> create(@Valid @RequestBody FileResourceRequestDTO dto) {
        try {
            logger.info("Creating new file resource with data={}", dto);
            FileResource entity = new FileResource();
            entity.setOriginType(dto.getOriginType());
            entity.setPath(dto.getPath());
            entity.setType(dto.getType());
            FileResource created = fileResourceService.create(entity);
            return ResponseEntity.created(URI.create("/api/file-resources/" + created.getId()))
                    .body(mapToDTO(created));
        } catch (Exception e) {
            logger.error("Error creating file resource: {}", e.getMessage());
            throw e;
        }
    }

    // ================= UPDATE FILE RESOURCE =================
    @Operation(summary = "Update file resource by ID")
    @PutMapping("/{id}")
    public ResponseEntity<FileResourceResponseDTO> update(@PathVariable Long id,
                                                          @Valid @RequestBody FileResourceRequestDTO dto) {
        try {
            logger.info("Updating file resource id={}", id);
            FileResource entity = new FileResource();
            entity.setOriginType(dto.getOriginType());
            entity.setPath(dto.getPath());
            entity.setType(dto.getType());
            FileResource updated = fileResourceService.update(id, entity);
            return ResponseEntity.ok(mapToDTO(updated));
        } catch (Exception e) {
            logger.error("Error updating file resource id={}: {}", id, e.getMessage());
            throw e;
        }
    }

    // ================= DELETE FILE RESOURCE =================
    @Operation(summary = "Delete file resource by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            logger.info("Deleting file resource id={}", id);
            fileResourceService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("Error deleting file resource id={}: {}", id, e.getMessage());
            throw e;
        }
    }
}

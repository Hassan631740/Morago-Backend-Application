package com.morago_backend.controller;

import com.morago_backend.dto.dtoRequest.CategoryRequestDTO;
import com.morago_backend.dto.dtoRequest.FilterRequest;
import com.morago_backend.dto.dtoRequest.PaginationRequest;
import com.morago_backend.dto.dtoResponse.CategoryResponseDTO;
import com.morago_backend.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
@PreAuthorize("hasRole('ADMINISTRATOR')")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Category Management - ADMIN", description = "APIs for managing categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private static final Logger logger = LoggerFactory.getLogger(CategoryController.class);

    // ========== GET ALL WITH FILTER & PAGINATION ==========
    @Operation(summary = "Get all categories with pagination and filtering")
    @GetMapping
    public ResponseEntity<Page<CategoryResponseDTO>> getAll(@Valid PaginationRequest pagination,
                                                            FilterRequest filter) {
        try {
            logger.info("Fetching categories with pagination={} filter={}", pagination, filter);
            Page<CategoryResponseDTO> page = categoryService.getAllWithFilterAndPagination(filter, pagination);
            return ResponseEntity.ok(page);
        } catch (Exception e) {
            logger.error("Error fetching categories with pagination and filter: {}", e.getMessage());
            throw e;
        }
    }

    // ========== GET ALL WITHOUT PAGINATION ==========
    @Operation(summary = "Get all categories (no pagination)")
    @GetMapping("/all")
    public ResponseEntity<List<CategoryResponseDTO>> getAllLegacy() {
        try {
            logger.info("Fetching all categories (legacy endpoint)");
            return ResponseEntity.ok(categoryService.findAllDTO());
        } catch (Exception e) {
            logger.error("Error fetching all categories (legacy): {}", e.getMessage());
            throw e;
        }
    }

    // ========== GET BY ID ==========
    @Operation(summary = "Get category by ID")
    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> getById(@PathVariable Long id) {
        try {
            logger.info("Fetching category by id={}", id);
            CategoryResponseDTO dto = categoryService.findByIdDTO(id)
                    .orElseThrow(() -> new com.morago_backend.exception.ResourceNotFoundException("Category not found with id " + id));
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            logger.error("Error fetching category id={}: {}", id, e.getMessage());
            throw e;
        }
    }

    // ========== CREATE ==========
    @Operation(summary = "Create new category")
    @PostMapping
    public ResponseEntity<CategoryResponseDTO> create(@Valid @RequestBody CategoryRequestDTO dto) {
        try {
            logger.info("Creating category with data={}", dto);
            CategoryResponseDTO created = categoryService.create(dto);
            return ResponseEntity.created(URI.create("/api/categories/" + created.getId()))
                    .body(created);
        } catch (Exception e) {
            logger.error("Error creating category: {}", e.getMessage());
            throw e;
        }
    }

    // ========== UPDATE ==========
    @Operation(summary = "Update category by ID")
    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> update(@PathVariable Long id,
                                                      @Valid @RequestBody CategoryRequestDTO dto) {
        try {
            logger.info("Updating category id={}", id);
            CategoryResponseDTO updated = categoryService.update(id, dto);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            logger.error("Error updating category id={}: {}", id, e.getMessage());
            throw e;
        }
    }

    // ========== DELETE ==========
    @Operation(summary = "Delete category by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            logger.info("Deleting category id={}", id);
            categoryService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("Error deleting category id={}: {}", id, e.getMessage());
            throw e;
        }
    }
}

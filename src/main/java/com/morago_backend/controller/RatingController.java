package com.morago_backend.controller;

import com.morago_backend.dto.dtoRequest.RatingRequestDTO;
import com.morago_backend.dto.dtoResponse.RatingResponseDTO;
import com.morago_backend.service.RatingService;
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
@RequestMapping("/api/ratings")
@PreAuthorize("hasRole('ADMINISTRATOR')")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Rating Management - ADMIN")
public class RatingController {

    private static final Logger logger = LoggerFactory.getLogger(RatingController.class);

    private final RatingService ratingService;

    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    // ========== GET ALL RATINGS ==========
    @Operation(summary = "Get all ratings")
    @GetMapping
    public ResponseEntity<List<RatingResponseDTO>> getAll() {
        try {
            logger.info("Fetching all ratings");
            return ResponseEntity.ok(ratingService.findAll());
        } catch (Exception ex) {
            logger.error("Error fetching ratings: {}", ex.getMessage(), ex);
            throw ex;
        }
    }

    // ========== GET RATING BY ID ==========
    @Operation(summary = "Get rating by ID")
    @GetMapping("/{id}")
    public ResponseEntity<RatingResponseDTO> getById(@PathVariable Long id) {
        try {
            logger.info("Fetching rating by id={}", id);
            RatingResponseDTO dto = ratingService.findById(id);
            return ResponseEntity.ok(dto);
        } catch (Exception ex) {
            logger.error("Error fetching rating id={}: {}", id, ex.getMessage(), ex);
            throw ex;
        }
    }

    // ========== CREATE NEW RATING ==========
    @Operation(summary = "Create new rating")
    @PostMapping
    public ResponseEntity<RatingResponseDTO> create(@Valid @RequestBody RatingRequestDTO dto) {
        try {
            logger.info("Creating new rating with data={}", dto);
            RatingResponseDTO created = ratingService.create(dto);
            return ResponseEntity.created(URI.create("/api/ratings/" + created.getId()))
                    .body(created);
        } catch (Exception ex) {
            logger.error("Error creating rating: {}", ex.getMessage(), ex);
            throw ex;
        }
    }

    // ========== UPDATE RATING ==========
    @Operation(summary = "Update rating by ID")
    @PutMapping("/{id}")
    public ResponseEntity<RatingResponseDTO> update(@PathVariable Long id,
                                                    @Valid @RequestBody RatingRequestDTO dto) {
        try {
            logger.info("Updating rating id={} with data={}", id, dto);
            RatingResponseDTO updated = ratingService.update(id, dto);
            return ResponseEntity.ok(updated);
        } catch (Exception ex) {
            logger.error("Error updating rating id={}: {}", id, ex.getMessage(), ex);
            throw ex;
        }
    }

    // ========== DELETE RATING ==========
    @Operation(summary = "Delete rating by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            logger.info("Deleting rating id={}", id);
            ratingService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (Exception ex) {
            logger.error("Error deleting rating id={}: {}", id, ex.getMessage(), ex);
            throw ex;
        }
    }
}

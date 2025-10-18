package com.morago_backend.controller;

import com.morago_backend.dto.dtoRequest.UserProfileRequestDTO;
import com.morago_backend.dto.dtoResponse.UserProfileResponseDTO;
import com.morago_backend.exception.ResourceNotFoundException;
import com.morago_backend.service.UserProfileService;
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
@RequestMapping("/api/user-profiles")
@PreAuthorize("hasRole('ADMINISTRATOR')")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "User Profile Management - ADMIN")
public class UserProfileController {

    private final UserProfileService service;
    private static final Logger logger = LoggerFactory.getLogger(UserProfileController.class);

    public UserProfileController(UserProfileService service) {
        this.service = service;
    }

    // ========== GET ALL ==========
    @GetMapping
    public ResponseEntity<List<UserProfileResponseDTO>> getAll() {
        try {
            logger.info("Fetching all user profiles");
            List<UserProfileResponseDTO> profiles = service.findAll();
            return ResponseEntity.ok(profiles);
        } catch (Exception ex) {
            logger.error("Error fetching all user profiles: {}", ex.getMessage(), ex);
            return ResponseEntity.internalServerError().build();
        }
    }

    // ========== GET BY ID ==========
    @GetMapping("/{id}")
    public ResponseEntity<UserProfileResponseDTO> getById(@PathVariable Long id) {
        try {
            logger.info("Fetching user profile by id={}", id);
            UserProfileResponseDTO dto = service.findById(id);
            if (dto == null) {
                logger.warn("UserProfile not found with id={}", id);
                throw new ResourceNotFoundException("UserProfile not found with id " + id);
            }
            return ResponseEntity.ok(dto);
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.notFound().build();
        } catch (Exception ex) {
            logger.error("Error fetching user profile id={}: {}", id, ex.getMessage(), ex);
            return ResponseEntity.internalServerError().build();
        }
    }

    // ========== CREATE ==========
    @PostMapping
    public ResponseEntity<UserProfileResponseDTO> create(@Valid @RequestBody UserProfileRequestDTO dto) {
        try {
            logger.info("Creating new user profile with data={}", dto);
            UserProfileResponseDTO created = service.create(dto);
            URI location = URI.create("/api/user-profiles/" + created.getId());
            return ResponseEntity.created(location).body(created);
        } catch (Exception ex) {
            logger.error("Error creating user profile: {}", ex.getMessage(), ex);
            return ResponseEntity.internalServerError().build();
        }
    }

    // ========== UPDATE ==========
    @PutMapping("/{id}")
    public ResponseEntity<UserProfileResponseDTO> update(@PathVariable Long id,
                                                         @Valid @RequestBody UserProfileRequestDTO dto) {
        try {
            logger.info("Updating user profile id={} with data={}", id, dto);
            UserProfileResponseDTO updated = service.update(id, dto);
            if (updated == null) {
                logger.warn("UserProfile not found for update with id={}", id);
                throw new ResourceNotFoundException("UserProfile not found with id " + id);
            }
            return ResponseEntity.ok(updated);
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.notFound().build();
        } catch (Exception ex) {
            logger.error("Error updating user profile id={}: {}", id, ex.getMessage(), ex);
            return ResponseEntity.internalServerError().build();
        }
    }

    // ========== DELETE ==========
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            logger.info("Deleting user profile id={}", id);
            service.delete(id);
            return ResponseEntity.noContent().build();
        } catch (Exception ex) {
            logger.error("Error deleting user profile id={}: {}", id, ex.getMessage(), ex);
            return ResponseEntity.internalServerError().build();
        }
    }
}

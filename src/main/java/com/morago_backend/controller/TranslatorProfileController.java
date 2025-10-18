package com.morago_backend.controller;

import com.morago_backend.dto.dtoRequest.FilterRequest;
import com.morago_backend.dto.dtoRequest.PaginationRequest;
import com.morago_backend.dto.dtoRequest.TranslatorProfileRequestDTO;
import com.morago_backend.dto.dtoResponse.PagedResponse;
import com.morago_backend.dto.dtoResponse.TranslatorProfileResponseDTO;
import com.morago_backend.exception.ResourceNotFoundException;
import com.morago_backend.service.TranslatorProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/translator-profiles")
@PreAuthorize("hasAnyRole('INTERPRETER','ADMINISTRATOR')")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Translator Profile Management", description = "APIs for managing translator profiles")
public class TranslatorProfileController {

    private final TranslatorProfileService service;
    private static final Logger logger = LoggerFactory.getLogger(TranslatorProfileController.class);

    public TranslatorProfileController(TranslatorProfileService service) {
        this.service = service;
    }

    // ========== GET ALL WITH FILTER AND PAGINATION ==========
    @Operation(summary = "Get all translator profiles with pagination and filtering")
    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRATOR')") // Only admin can see all profiles
    public ResponseEntity<PagedResponse<TranslatorProfileResponseDTO>> getAll(
            @Parameter(description = "Pagination parameters") @Valid PaginationRequest pagination,
            @Parameter(description = "Filter parameters") FilterRequest filter) {
        try {
            logger.info("Fetching all translator profiles with pagination={} and filter={}", pagination, filter);
            var page = service.getAllWithFilterAndPagination(filter, pagination);
            var response = new PagedResponse<>(page.getContent(), page.getNumber(), page.getSize(), page.getTotalElements());
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            logger.error("Error fetching translator profiles: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ========== GET ALL LEGACY ==========
    @Operation(summary = "Get all translator profiles (legacy endpoint without pagination)")
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMINISTRATOR')") // Only admin can see all profiles
    public ResponseEntity<List<TranslatorProfileResponseDTO>> getAllLegacy() {
        try {
            logger.info("Fetching all translator profiles (legacy endpoint)");
            List<TranslatorProfileResponseDTO> list = service.findAll();
            return ResponseEntity.ok(list);
        } catch (Exception ex) {
            logger.error("Error fetching all translator profiles: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ========== GET BY ID ==========
    @Operation(summary = "Get translator profile by ID")
    @GetMapping("/{id}")
    public ResponseEntity<TranslatorProfileResponseDTO> getById(@PathVariable Long id) {
        try {
            logger.info("Fetching translator profile by id={}", id);

            // Get current authenticated user
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Object principal = auth.getPrincipal();

            String currentPhone = null;
            boolean isInterpreter = false;

            if (principal instanceof UserDetails user) {
                currentPhone = user.getUsername(); // username = phone
                isInterpreter = user.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_INTERPRETER"));
            }

            // If role is INTERPRETER, allow access only to own profile
            if (isInterpreter) {
                TranslatorProfileResponseDTO ownProfile = service.findByPhone(currentPhone);
                if (!id.equals(ownProfile.getId())) {
                    logger.warn("Interpreter with phone={} attempted to access another profile id={}", currentPhone, id);
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                }
            }

            TranslatorProfileResponseDTO dto = service.findById(id);
            if (dto == null) {
                logger.warn("Translator profile not found with id={}", id);
                throw new ResourceNotFoundException("TranslatorProfile not found with id " + id);
            }

            return ResponseEntity.ok(dto);
        } catch (ResourceNotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            logger.error("Error fetching translator profile id={}: {}", id, ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    // ========== UPDATE PROFILE ==========
    @Operation(summary = "Update translator profile by ID")
    @PutMapping("/{id}")
    public ResponseEntity<TranslatorProfileResponseDTO> update(@PathVariable Long id,
                                                               @Valid @RequestBody TranslatorProfileRequestDTO dto,
                                                               Authentication auth) {
        try {
            logger.info("Updating translator profile id={} with data={}", id, dto);

            UserDetails user = (UserDetails) auth.getPrincipal();
            String currentUserPhone = user.getUsername();
            boolean isInterpreter = user.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_INTERPRETER"));

            if (isInterpreter) {
                TranslatorProfileResponseDTO ownProfile = service.findByPhone(currentUserPhone);
                if (ownProfile == null || !ownProfile.getId().equals(id)) {
                    logger.warn("Interpreter {} attempted to update another profile id={}", currentUserPhone, id);
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                }
            }

            TranslatorProfileResponseDTO updated = service.update(id, dto);
            return ResponseEntity.ok(updated);
        } catch (Exception ex) {
            logger.error("Error updating translator profile id={}: {}", id, ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/availability")
    public ResponseEntity<TranslatorProfileResponseDTO> updateAvailability(@RequestParam Boolean isAvailable) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication(); // move here
        try {
            UserDetails user = (UserDetails) auth.getPrincipal();
            boolean isInterpreter = user.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_INTERPRETER"));

            if (!isInterpreter) {
                logger.warn("User {} attempted to update availability but is not a translator", user.getUsername());
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            Long currentUserId = Long.valueOf(user.getUsername()); // assuming username stores user ID
            TranslatorProfileResponseDTO updated = service.updateAvailability(currentUserId, isAvailable);
            return ResponseEntity.ok(updated);

        } catch (Exception ex) {
            logger.error("Error updating availability for user {}: {}", auth.getName(), ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ========== UPDATE THEMES ==========
    @Operation(summary = "Update translator themes (Many-to-Many)")
    @PutMapping("/{id}/themes")
    public ResponseEntity<TranslatorProfileResponseDTO> updateThemes(@PathVariable Long id,
                                                                     @RequestBody List<Long> themeIds,
                                                                     Authentication auth) {
        try {
            logger.info("Updating themes for translator id={} with themes={}", id, themeIds);

            UserDetails user = (UserDetails) auth.getPrincipal();
            String currentUserPhone = user.getUsername();
            boolean isInterpreter = user.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_INTERPRETER"));

            if (isInterpreter) {
                TranslatorProfileResponseDTO ownProfile = service.findByPhone(currentUserPhone);
                if (ownProfile == null || !ownProfile.getId().equals(id)) {
                    logger.warn("Interpreter {} attempted to update themes for another profile id={}", currentUserPhone, id);
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                }
            }

            TranslatorProfileResponseDTO updated = service.updateThemes(id, themeIds);
            return ResponseEntity.ok(updated);
        } catch (Exception ex) {
            logger.error("Error updating themes for translator id={}: {}", id, ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ========== VERIFY PROFILE ==========
    @Operation(summary = "Verify translator profile", description = "Admin-only endpoint to verify a translator's profile")
    @PostMapping("/{id}/verify")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<TranslatorProfileResponseDTO> verifyProfile(@PathVariable Long id) {
        TranslatorProfileResponseDTO verified = service.verifyProfile(id);
        return ResponseEntity.ok(verified);
    }

    // ========== UNVERIFY PROFILE ==========
    @Operation(summary = "Unverify translator profile", description = "Admin-only endpoint to remove verification from a translator's profile")
    @PostMapping("/{id}/unverify")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<TranslatorProfileResponseDTO> unverifyProfile(@PathVariable Long id) {
        TranslatorProfileResponseDTO unverified = service.unverifyProfile(id);
        return ResponseEntity.ok(unverified);
    }

    // ========== GET UNVERIFIED PROFILES ==========
    @Operation(summary = "Get all unverified translator profiles", description = "Admin-only endpoint to list all unverified translator profiles")
    @GetMapping("/unverified")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<List<TranslatorProfileResponseDTO>> getUnverifiedProfiles() {
        List<TranslatorProfileResponseDTO> unverified = service.findUnverifiedProfiles();
        return ResponseEntity.ok(unverified);
    }
}

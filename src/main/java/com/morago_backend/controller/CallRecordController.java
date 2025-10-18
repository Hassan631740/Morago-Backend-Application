package com.morago_backend.controller;

import com.morago_backend.dto.dtoRequest.CallRecordRequestDTO;
import com.morago_backend.dto.dtoResponse.CallRecordResponseDTO;
import com.morago_backend.service.CallRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/calls")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Call Records - All roles", description = "APIs for managing call records")
@RequiredArgsConstructor
public class CallRecordController {

    private final CallRecordService callRecordService;
    private static final Logger logger = LoggerFactory.getLogger(CallRecordController.class);

    // ========== GET ALL CALL RECORDS ==========
    @Operation(summary = "Get all call records")
    @GetMapping
    public ResponseEntity<List<CallRecordResponseDTO>> getAll() {
        try {
            logger.info("Fetching all call records");
            return ResponseEntity.ok(callRecordService.findAll());
        } catch (Exception e) {
            logger.error("Error fetching call records: {}", e.getMessage());
            throw e;
        }
    }

    // ========== GET CALL RECORD BY ID ==========
    @Operation(summary = "Get call record by ID")
    @GetMapping("/{id}")
    public ResponseEntity<CallRecordResponseDTO> getById(@PathVariable Long id) {
        try {
            logger.info("Fetching call record by id={}", id);
            return ResponseEntity.ok(callRecordService.findById(id));
        } catch (Exception e) {
            logger.error("Error fetching call record id={}: {}", id, e.getMessage());
            throw e;
        }
    }

    // ========== CREATE NEW CALL RECORD ==========
    @Operation(summary = "Create new call record")
    @PostMapping
    public ResponseEntity<CallRecordResponseDTO> create(@Valid @RequestBody CallRecordRequestDTO dto) {
        try {
            logger.info("Creating new call record");
            CallRecordResponseDTO created = callRecordService.create(dto);
            return ResponseEntity.created(URI.create("/api/calls/" + created.getId()))
                    .body(created);
        } catch (Exception e) {
            logger.error("Error creating call record: {}", e.getMessage());
            throw e;
        }
    }

    // ========== UPDATE CALL RECORD ==========
    @Operation(summary = "Update call record by ID")
    @PutMapping("/{id}")
    public ResponseEntity<CallRecordResponseDTO> update(@PathVariable Long id,
                                                        @Valid @RequestBody CallRecordRequestDTO dto) {
        try {
            logger.info("Updating call record id={}", id);
            return ResponseEntity.ok(callRecordService.update(id, dto));
        } catch (Exception e) {
            logger.error("Error updating call record id={}: {}", id, e.getMessage());
            throw e;
        }
    }

    // ========== DELETE CALL RECORD ==========
    @Operation(summary = "Delete call record by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            logger.info("Deleting call record id={}", id);
            callRecordService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("Error deleting call record id={}: {}", id, e.getMessage());
            throw e;
        }
    }
}

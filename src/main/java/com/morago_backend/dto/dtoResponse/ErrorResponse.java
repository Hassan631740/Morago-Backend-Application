package com.morago_backend.dto.dtoResponse;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;

/**
 * Standard error response format for API
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Standard error response format")
public class ErrorResponse {
    
    @Schema(description = "Error timestamp", example = "2025-10-18T10:30:00.000Z")
    private Instant timestamp;
    
    @Schema(description = "HTTP status code", example = "400")
    private int status;
    
    @Schema(description = "Error type", example = "Bad Request")
    private String error;
    
    @Schema(description = "Detailed error message", example = "Validation failed")
    private String message;
    
    @Schema(description = "Request path", example = "/api/auth/login")
    private String path;
    
    @Schema(description = "Additional error details (optional)", example = "{\"field\": \"email\", \"error\": \"must be a valid email\"}")
    private Map<String, Object> details;
}

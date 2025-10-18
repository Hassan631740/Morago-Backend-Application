package com.morago_backend.dto.dtoRequest;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO for pagination parameters
 */
@Setter
@Getter
@Schema(description = "Pagination parameters for API requests")
public class PaginationRequest {

    // Page number (0-based)
    @Schema(description = "Page number (0-based)", example = "0", minimum = "0")
    @Min(value = 0, message = "Page number must be 0 or greater")
    private int page = 0;

    // Number of items per page
    @Schema(description = "Number of items per page", example = "10", minimum = "1", maximum = "100")
    @Min(value = 1, message = "Size must be at least 1")
    @Max(value = 100, message = "Size cannot exceed 100")
    private int size = 10;

    // Field to sort by
    @Schema(description = "Sort field", example = "id")
    private String sortBy = "id";

    // Sort direction: "asc" or "desc"
    @Schema(description = "Sort direction", example = "asc", allowableValues = {"asc", "desc"})
    private String sortDirection = "asc";

    // Default constructor required by Spring for binding
    public PaginationRequest() {}

    // Constructor with ascending boolean
    public PaginationRequest(int page, int size, String sortBy, boolean ascending) {
        this.page = page;
        this.size = size;
        this.sortBy = sortBy;
        this.sortDirection = ascending ? "asc" : "desc";
    }

    // Constructor with explicit sortDirection
    public PaginationRequest(int page, int size, String sortBy, String sortDirection) {
        this.page = page;
        this.size = size;
        this.sortBy = sortBy;
        this.sortDirection = sortDirection;
    }

    // Check if sort direction is ascending
    public boolean isAscending() {
        return "asc".equalsIgnoreCase(sortDirection);
    }
}

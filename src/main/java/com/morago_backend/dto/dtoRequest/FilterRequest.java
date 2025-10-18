package com.morago_backend.dto.dtoRequest;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * DTO for filtering parameters
 */
@Setter
@Getter
@Schema(description = "Filter parameters for API requests")
public class FilterRequest {

    // Getters and Setters
    @Schema(description = "Search term for text-based filtering", example = "john")
    private String search;
    
    @Schema(description = "Map of field-specific filters", example = "{\"status\": \"active\", \"role\": \"USER\"}")
    private Map<String, Object> filters;
    
    @Schema(description = "Date range filter - start date (ISO format)", example = "2023-01-01T00:00:00")
    private String dateFrom;
    
    @Schema(description = "Date range filter - end date (ISO format)", example = "2023-12-31T23:59:59")
    private String dateTo;
    
    public FilterRequest() {}
    
    public FilterRequest(String search, Map<String, Object> filters, String dateFrom, String dateTo) {
        this.search = search;
        this.filters = filters;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
    }

    public boolean hasSearch() {
        return search != null && !search.trim().isEmpty();
    }
    
    public boolean hasFilters() {
        return filters != null && !filters.isEmpty();
    }
    
    public boolean hasDateRange() {
        return dateFrom != null || dateTo != null;
    }
}

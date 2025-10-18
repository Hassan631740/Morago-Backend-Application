package com.morago_backend.util;

import com.morago_backend.dto.dtoRequest.FilterRequest;
import com.morago_backend.dto.dtoRequest.PaginationRequest;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * Utility class for handling query parameters and building filter/pagination requests
 */
public class QueryParameterUtils {
    
    /**
     * Build PaginationRequest from query parameters
     */
    public static PaginationRequest buildPaginationRequest(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {
        
        return new PaginationRequest(page, size, sortBy, sortDirection);
    }
    
    /**
     * Build FilterRequest from query parameters
     */
    public static FilterRequest buildFilterRequest(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Map<String, Object> filters,
            @RequestParam(required = false) String dateFrom,
            @RequestParam(required = false) String dateTo) {
        
        return new FilterRequest(search, filters, dateFrom, dateTo);
    }
    
    /**
     * Build FilterRequest with simple search
     */
    public static FilterRequest buildSimpleFilterRequest(String search) {
        return new FilterRequest(search, null, null, null);
    }
    
    /**
     * Build FilterRequest with role filter
     */
    public static FilterRequest buildRoleFilterRequest(String role) {
        Map<String, Object> filters = Map.of("role", role);
        return new FilterRequest(null, filters, null, null);
    }
    
    /**
     * Build FilterRequest with active status filter
     */
    public static FilterRequest buildActiveFilterRequest(Boolean active) {
        Map<String, Object> filters = Map.of("active", active);
        return new FilterRequest(null, filters, null, null);
    }
}

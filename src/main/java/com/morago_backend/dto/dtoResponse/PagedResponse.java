package com.morago_backend.dto.dtoResponse;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Generic response wrapper for paginated data
 */
@Setter
@Getter
@Schema(description = "Paginated response wrapper")
public class PagedResponse<T> {

    // Getters and Setters
    @Schema(description = "List of items in current page")
    private List<T> content;
    
    @Schema(description = "Current page number (0-based)")
    private int page;
    
    @Schema(description = "Number of items per page")
    private int size;
    
    @Schema(description = "Total number of items across all pages")
    private long totalElements;
    
    @Schema(description = "Total number of pages")
    private int totalPages;
    
    @Schema(description = "Whether this is the first page")
    private boolean first;
    
    @Schema(description = "Whether this is the last page")
    private boolean last;
    
    @Schema(description = "Number of items in current page")
    private int numberOfElements;
    
    @Schema(description = "Whether there are more pages")
    private boolean hasNext;
    
    @Schema(description = "Whether there are previous pages")
    private boolean hasPrevious;
    
    public PagedResponse() {}
    
    public PagedResponse(List<T> content, int page, int size, long totalElements) {
        this.content = content;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = (int) Math.ceil((double) totalElements / size);
        this.first = page == 0;
        this.last = page >= totalPages - 1;
        this.numberOfElements = content.size();
        this.hasNext = !last;
        this.hasPrevious = !first;
    }

}

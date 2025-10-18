package com.morago_backend.service;

import com.morago_backend.dto.dtoRequest.FilterRequest;
import com.morago_backend.dto.dtoResponse.PagedResponse;
import com.morago_backend.dto.dtoRequest.PaginationRequest;

import java.util.List;
import java.util.Optional;

/**
 * Base service interface providing common CRUD operations with pagination and filtering
 */
public interface BaseService<T, ID> {
    
    /**
     * Find all entities with pagination and filtering
     */
    PagedResponse<T> findAll(PaginationRequest pagination, FilterRequest filter);
    
    /**
     * Find all entities without pagination (for backward compatibility)
     */
    List<T> findAll();
    
    /**
     * Find entity by ID
     */
    Optional<T> findById(ID id);
    
    /**
     * Create new entity
     */
    T create(T entity);
    
    /**
     * Update existing entity
     */
    T update(ID id, T entity);
    
    /**
     * Delete entity by ID
     */
    void delete(ID id);
    
    /**
     * Count total entities matching filter criteria
     */
    long count(FilterRequest filter);
}

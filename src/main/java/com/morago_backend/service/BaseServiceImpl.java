package com.morago_backend.service;

import com.morago_backend.dto.dtoRequest.FilterRequest;
import com.morago_backend.dto.dtoRequest.PaginationRequest;
import com.morago_backend.dto.dtoResponse.PagedResponse;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Base service providing common CRUD operations with pagination and filtering.
 */
public abstract class BaseServiceImpl<T, ID> implements BaseService<T, ID> {

    protected abstract JpaRepository<T, ID> getRepository();

    //=== Find all with pagination and optional filtering ===//
    @Override
    public PagedResponse<T> findAll(PaginationRequest pagination, FilterRequest filter) {
        Sort sort = Sort.by(pagination.isAscending() ? Sort.Direction.ASC : Sort.Direction.DESC,
                pagination.getSortBy());
        Pageable pageable = PageRequest.of(pagination.getPage(), pagination.getSize(), sort);

        Page<T> page = applyFilters(getRepository().findAll(pageable), filter);

        return new PagedResponse<>(page.getContent(), page.getNumber(), page.getSize(), page.getTotalElements());
    }

    //=== Find all without pagination ===//
    @Override
    public List<T> findAll() {
        return getRepository().findAll();
    }

    //=== Find by ID ===//
    @Override
    public Optional<T> findById(ID id) {
        return getRepository().findById(id);
    }

    //=== Create entity ===//
    @Override
    public T create(T entity) {
        return getRepository().save(entity);
    }

    //=== Update entity by ID ===//
    @Override
    public T update(ID id, T entity) {
        if (!getRepository().existsById(id)) {
            throw new RuntimeException("Entity not found");
        }
        return getRepository().save(entity);
    }

    //=== Delete entity by ID ===//
    @Override
    public void delete(ID id) {
        getRepository().deleteById(id);
    }

    //=== Count entities (subclass can override for filtered count) ===//
    @Override
    public long count(FilterRequest filter) {
        return getRepository().count();
    }

    //=== Apply filters (override in subclass if needed) ===//
    protected Page<T> applyFilters(Page<T> page, FilterRequest filter) {
        return page;
    }
}

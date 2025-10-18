package com.morago_backend.service;

import com.morago_backend.dto.dtoRequest.CategoryRequestDTO;
import com.morago_backend.dto.dtoRequest.FilterRequest;
import com.morago_backend.dto.dtoRequest.PaginationRequest;
import com.morago_backend.dto.dtoResponse.CategoryResponseDTO;
import com.morago_backend.entity.Category;
import com.morago_backend.exception.ResourceNotFoundException;
import com.morago_backend.repository.CategoryRepository;
import com.corundumstudio.socketio.SocketIOServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoryService extends BaseServiceImpl<Category, Long> {

    private final CategoryRepository categoryRepository;
    private final SocketIOServer socketServer;
    private static final Logger logger = LoggerFactory.getLogger(CategoryService.class);

    public CategoryService(CategoryRepository categoryRepository, SocketIOServer socketServer) {
        this.categoryRepository = categoryRepository;
        this.socketServer = socketServer;
    }

    @Override
    protected JpaRepository<Category, Long> getRepository() {
        return categoryRepository;
    }

    @Override
    protected Page<Category> applyFilters(Page<Category> page, FilterRequest filter) {
        try {
            if (filter == null || !filter.hasSearch()) return page;

            String term = filter.getSearch().toLowerCase();
            List<Category> filtered = page.getContent().stream()
                    .filter(c -> c.getName().toLowerCase().contains(term))
                    .collect(Collectors.toList());

            return new PageImpl<>(filtered, page.getPageable(), page.getTotalElements());
        } catch (Exception e) {
            logger.error("Error applying filters to categories", e);
            throw e;
        }
    }

    // ========== CREATE ==========
    /** Creates a new category and emits a socket event */
    public CategoryResponseDTO create(CategoryRequestDTO dto) {
        try {
            logger.info("Creating category with data={}", dto);
            Category category = mapToEntity(dto);
            Category saved = categoryRepository.save(category);
            socketServer.getBroadcastOperations().sendEvent("categoryCreated", saved);
            logger.info("Category created id={}", saved.getId());
            return mapToResponse(saved);
        } catch (Exception e) {
            logger.error("Error creating category", e);
            throw e;
        }
    }

    // ========== UPDATE ==========
    /** Updates a category by ID */
    public CategoryResponseDTO update(Long id, CategoryRequestDTO dto) {
        try {
            logger.info("Updating category id={}", id);
            Category existing = categoryRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id " + id));

            existing.setName(dto.getName());
            existing.setIsActive(dto.getIsActive());

            Category saved = categoryRepository.save(existing);
            socketServer.getBroadcastOperations().sendEvent("categoryUpdated", saved);
            logger.info("Category updated id={}", saved.getId());
            return mapToResponse(saved);
        } catch (Exception e) {
            logger.error("Error updating category id={}", id, e);
            throw e;
        }
    }

    // ========== READ ALL DTO ==========
    /** Retrieves all categories without pagination */
    public List<CategoryResponseDTO> findAllDTO() {
        try {
            logger.info("Fetching all categories (no pagination)");
            return categoryRepository.findAll().stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error fetching all categories", e);
            throw e;
        }
    }

    // ========== READ BY ID DTO ==========
    /** Retrieves a category by ID as DTO */
    public Optional<CategoryResponseDTO> findByIdDTO(Long id) {
        try {
            return categoryRepository.findById(id).map(this::mapToResponse);
        } catch (Exception e) {
            logger.error("Error fetching category id={}", id, e);
            throw e;
        }
    }

    // ========== DELETE ==========
    /** Deletes a category and emits a socket event */
    @Override
    public void delete(Long id) {
        try {
            logger.info("Deleting category id={}", id);
            categoryRepository.deleteById(id);
            socketServer.getBroadcastOperations().sendEvent("categoryDeleted", id);
            logger.info("Category deleted id={}", id);
        } catch (Exception e) {
            logger.error("Error deleting category id={}", id, e);
            throw e;
        }
    }

    // ========== GET ALL WITH FILTER & PAGINATION ==========
    /** Retrieves categories with filter and pagination */
    public Page<CategoryResponseDTO> getAllWithFilterAndPagination(FilterRequest filter, PaginationRequest pagination) {
        try {
            Pageable pageable = PageRequest.of(pagination.getPage(), pagination.getSize());
            Page<Category> page = categoryRepository.findAll(pageable);

            if (filter != null && filter.hasSearch()) {
                List<Category> filtered = page.getContent().stream()
                        .filter(c -> c.getName().toLowerCase().contains(filter.getSearch().toLowerCase()))
                        .toList();
                page = new PageImpl<>(filtered, page.getPageable(), page.getTotalElements());
            }

            return page.map(this::mapToResponse);
        } catch (Exception e) {
            logger.error("Error fetching categories with filter and pagination", e);
            throw e;
        }
    }

    // ========== MAPPER ==========
    private CategoryResponseDTO mapToResponse(Category category) {
        CategoryResponseDTO dto = new CategoryResponseDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setIsActive(category.getIsActive());
        dto.setCreatedAtDatetime(category.getCreatedAt());
        dto.setUpdatedAtDatetime(category.getUpdatedAt());
        return dto;
    }

    private Category mapToEntity(CategoryRequestDTO dto) {
        Category category = new Category();
        category.setName(dto.getName());
        category.setIsActive(dto.getIsActive());
        return category;
    }
}

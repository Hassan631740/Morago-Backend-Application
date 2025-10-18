package com.morago_backend.service;

import com.morago_backend.dto.dtoRequest.ThemeRequestDTO;
import com.morago_backend.dto.dtoResponse.ThemeResponseDTO;
import com.morago_backend.entity.Theme;
import com.morago_backend.entity.Category;
import com.morago_backend.exception.ResourceNotFoundException;
import com.morago_backend.repository.ThemeRepository;
import com.morago_backend.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ThemeService {

    private static final Logger logger = LoggerFactory.getLogger(ThemeService.class);

    private final ThemeRepository themeRepository;
    private final CategoryRepository categoryRepository;

    // ========== CREATE ==========
    public ThemeResponseDTO create(ThemeRequestDTO dto) {
        try {
            logger.info("Creating theme {}", dto.getName());
            Theme theme = mapToEntity(dto);
            theme.setCreatedAt(LocalDateTime.now());
            theme.setUpdatedAt(LocalDateTime.now());
            Theme saved = themeRepository.save(theme);
            logger.info("Theme created with id={}", saved.getId());
            return mapToResponseDTO(saved);
        } catch (Exception e) {
            logger.error("Error creating theme {}", dto.getName(), e);
            throw e;
        }
    }

    // ========== READ ALL ==========
    public List<ThemeResponseDTO> findAll() {
        try {
            logger.info("Fetching all themes");
            return themeRepository.findAll().stream()
                    .map(this::mapToResponseDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error fetching all themes", e);
            throw e;
        }
    }

    // ========== READ by ID ==========
    public ThemeResponseDTO findById(Long id) {
        try {
            logger.info("Fetching theme with id={}", id);
            Theme theme = themeRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Theme not found with id " + id));
            return mapToResponseDTO(theme);
        } catch (Exception e) {
            logger.error("Error fetching theme with id={}", id, e);
            throw e;
        }
    }

    // ========== UPDATE ==========
    public ThemeResponseDTO update(Long id, ThemeRequestDTO dto) {
        try {
            logger.info("Updating theme with id={}", id);
            Theme existing = themeRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Theme not found with id " + id));

            existing.setName(dto.getName());
            existing.setKoreanTitle(dto.getKoreanTitle());
            existing.setPrice(dto.getPrice());
            existing.setNightPrice(dto.getNightPrice());
            existing.setDescription(dto.getDescription());
            existing.setIsPopular(dto.getIsPopular());
            existing.setIconId(dto.getIconId());
            existing.setIsActive(dto.getIsActive());
            existing.setUpdatedAt(LocalDateTime.now());

            if (dto.getCategoryId() != null) {
                Category category = categoryRepository.findById(dto.getCategoryId())
                        .orElseThrow(() -> new ResourceNotFoundException("Category not found with id " + dto.getCategoryId()));
                existing.setCategory(category);
            }

            Theme saved = themeRepository.save(existing);
            logger.info("Theme updated with id={}", saved.getId());
            return mapToResponseDTO(saved);
        } catch (Exception e) {
            logger.error("Error updating theme with id={}", id, e);
            throw e;
        }
    }

    // ========== DELETE ==========
    public void delete(Long id) {
        try {
            logger.info("Deleting theme with id={}", id);
            if (!themeRepository.existsById(id)) {
                logger.warn("Theme not found with id={}", id);
                throw new ResourceNotFoundException("Theme not found with id " + id);
            }
            themeRepository.deleteById(id);
            logger.info("Theme deleted with id={}", id);
        } catch (Exception e) {
            logger.error("Error deleting theme with id={}", id, e);
            throw e;
        }
    }

    // ========== MAPPERS ==========
    private ThemeResponseDTO mapToResponseDTO(Theme theme) {
        ThemeResponseDTO dto = new ThemeResponseDTO();
        dto.setId(theme.getId());
        dto.setName(theme.getName());
        dto.setKoreanTitle(theme.getKoreanTitle());
        dto.setPrice(theme.getPrice());
        dto.setNightPrice(theme.getNightPrice());
        dto.setDescription(theme.getDescription());
        dto.setIsPopular(theme.getIsPopular());
        dto.setIconId(theme.getIconId());
        dto.setIsActive(theme.getIsActive());
        dto.setCategoryId(theme.getCategory() != null ? theme.getCategory().getId() : null);
        dto.setCreatedAtDatetime(theme.getCreatedAt());
        dto.setUpdatedAtDatetime(theme.getUpdatedAt());
        return dto;
    }

    private Theme mapToEntity(ThemeRequestDTO dto) {
        Theme theme = new Theme();
        theme.setName(dto.getName());
        theme.setKoreanTitle(dto.getKoreanTitle());
        theme.setPrice(dto.getPrice());
        theme.setNightPrice(dto.getNightPrice());
        theme.setDescription(dto.getDescription());
        theme.setIsPopular(dto.getIsPopular());
        theme.setIconId(dto.getIconId());
        theme.setIsActive(dto.getIsActive());

        if (dto.getCategoryId() != null) {
            Category category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id " + dto.getCategoryId()));
            theme.setCategory(category);
        }
        return theme;
    }
}

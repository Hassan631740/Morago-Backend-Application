package com.morago_backend.service;

import com.morago_backend.dto.dtoRequest.FilterRequest;
import com.morago_backend.dto.dtoRequest.PaginationRequest;
import com.morago_backend.dto.dtoRequest.LanguageRequestDTO;
import com.morago_backend.dto.dtoResponse.LanguageResponseDTO;
import com.morago_backend.dto.dtoResponse.PagedResponse;
import com.morago_backend.entity.Language;
import com.morago_backend.exception.ResourceNotFoundException;
import com.morago_backend.repository.LanguageRepository;
import com.corundumstudio.socketio.SocketIOServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LanguageService {

    private final LanguageRepository repository;
    private final SocketIOServer socketServer;
    private static final Logger logger = LoggerFactory.getLogger(LanguageService.class);

    public LanguageService(LanguageRepository repository, SocketIOServer socketServer) {
        this.repository = repository;
        this.socketServer = socketServer;
    }

    private LanguageResponseDTO toDTO(Language entity) {
        LanguageResponseDTO dto = new LanguageResponseDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setCreatedAtDatetime(entity.getCreatedAtDatetime());
        dto.setUpdatedAtDatetime(entity.getUpdatedAtDatetime());
        return dto;
    }

    private Language fromDTO(LanguageRequestDTO dto) {
        Language entity = new Language();
        entity.setName(dto.getName());
        return entity;
    }

    // ========== CREATE ==========
    public LanguageResponseDTO create(LanguageRequestDTO dto) {
        try {
            logger.info("Creating language with name={}", dto.getName());
            Language saved = repository.save(fromDTO(dto));
            socketServer.getBroadcastOperations().sendEvent("languageCreated", saved);
            logger.info("Language created with id={}", saved.getId());
            return toDTO(saved);
        } catch (Exception e) {
            logger.error("Error creating language with name={}", dto.getName(), e);
            throw e;
        }
    }

    // ========== READ all ==========
    public List<LanguageResponseDTO> findAll() {
        try {
            logger.info("Fetching all languages");
            return repository.findAll().stream()
                    .map(this::toDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error fetching all languages", e);
            throw e;
        }
    }

    // ========== READ paged + filtered ==========
    public PagedResponse<LanguageResponseDTO> findAll(PaginationRequest pagination, FilterRequest filter) {
        try {
            PageRequest pageRequest = PageRequest.of(pagination.getPage(), pagination.getSize());
            Page<Language> page = repository.findAll(pageRequest);

            if (filter != null && filter.hasSearch()) {
                String searchTerm = filter.getSearch().toLowerCase();
                page = new org.springframework.data.domain.PageImpl<>(
                        page.getContent().stream()
                                .filter(l -> l.getName().toLowerCase().contains(searchTerm))
                                .collect(Collectors.toList()),
                        pageRequest,
                        page.getTotalElements()
                );
            }

            List<LanguageResponseDTO> content = page.getContent().stream()
                    .map(this::toDTO)
                    .collect(Collectors.toList());
            return new PagedResponse<>(content, page.getNumber(), page.getSize(), page.getTotalElements());
        } catch (Exception e) {
            logger.error("Error fetching languages with pagination/filter", e);
            throw e;
        }
    }

    // ========== READ by ID ==========
    public LanguageResponseDTO findById(Long id) {
        try {
            logger.info("Fetching language by id={}", id);
            return repository.findById(id)
                    .map(this::toDTO)
                    .orElseThrow(() -> {
                        logger.warn("Language not found with id={}", id);
                        return new ResourceNotFoundException("Language not found with id " + id);
                    });
        } catch (Exception e) {
            logger.error("Error fetching language by id={}", id, e);
            throw e;
        }
    }

    // ========== UPDATE ==========
    public LanguageResponseDTO update(Long id, LanguageRequestDTO dto) {
        try {
            logger.info("Updating language id={}", id);
            Language existing = repository.findById(id)
                    .orElseThrow(() -> {
                        logger.warn("Language not found with id={}", id);
                        return new ResourceNotFoundException("Language not found with id " + id);
                    });

            existing.setName(dto.getName());
            Language saved = repository.save(existing);
            socketServer.getBroadcastOperations().sendEvent("languageUpdated", saved);
            logger.info("Language updated id={}", saved.getId());
            return toDTO(saved);
        } catch (Exception e) {
            logger.error("Error updating language id={}", id, e);
            throw e;
        }
    }

    // ========== DELETE ==========
    public void delete(Long id) {
        try {
            logger.info("Deleting language id={}", id);
            if (!repository.existsById(id)) {
                logger.warn("Language not found with id={}", id);
                throw new ResourceNotFoundException("Language not found with id " + id);
            }
            repository.deleteById(id);
            socketServer.getBroadcastOperations().sendEvent("languageDeleted", id);
            logger.info("Language deleted id={}", id);
        } catch (Exception e) {
            logger.error("Error deleting language id={}", id, e);
            throw e;
        }
    }
}

package com.morago_backend.service;

import com.corundumstudio.socketio.SocketIOServer;
import com.morago_backend.dto.dtoRequest.FilterRequest;
import com.morago_backend.dto.dtoRequest.PaginationRequest;
import com.morago_backend.dto.dtoRequest.TranslatorProfileRequestDTO;
import com.morago_backend.dto.dtoResponse.TranslatorProfileResponseDTO;
import com.morago_backend.entity.Theme;
import com.morago_backend.entity.TranslatorProfile;
import com.morago_backend.exception.ResourceNotFoundException;
import com.morago_backend.repository.ThemeRepository;
import com.morago_backend.repository.TranslatorProfileRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TranslatorProfileService {

    private static final Logger logger = LoggerFactory.getLogger(TranslatorProfileService.class);

    private final TranslatorProfileRepository repository;
    private final ThemeRepository themeRepository;
    private final SocketIOServer socketServer;

    // ========== READ ALL ==========
    public List<TranslatorProfileResponseDTO> findAll() {
        try {
            logger.info("Fetching all translatorProfiles");
            return repository.findAll().stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error fetching all translatorProfiles", e);
            throw e;
        }
    }

    // ========== READ by ID ==========
    public TranslatorProfileResponseDTO findById(Long id) {
        try {
            logger.info("Fetching translatorProfile with id={}", id);
            TranslatorProfile entity = repository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("TranslatorProfile not found with id " + id));
            return mapToResponse(entity);
        } catch (Exception e) {
            logger.error("Error fetching translatorProfile with id={}", id, e);
            throw e;
        }
    }

    // ========== FIND BY PHONE ==========
    public TranslatorProfileResponseDTO findByPhone(String phone) {
        try {
            logger.info("Fetching translatorProfile by phone={}", phone);
            TranslatorProfile entity = repository.findByUserPhone(phone)
                    .orElseThrow(() -> new ResourceNotFoundException("TranslatorProfile not found with phone " + phone));
            return mapToResponse(entity);
        } catch (Exception e) {
            logger.error("Error fetching translatorProfile by phone={}", phone, e);
            throw e;
        }
    }


    // ========== UPDATE ==========
    public TranslatorProfileResponseDTO update(Long id, TranslatorProfileRequestDTO dto) {
        try {
            logger.info("Updating translatorProfile with id={}", id);
            TranslatorProfile existing = repository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("TranslatorProfile not found with id " + id));

            if (dto.getEmail() != null) existing.setEmail(dto.getEmail());
            if (dto.getDateOfBirth() != null) existing.setDateOfBirth(dto.getDateOfBirth());
            if (dto.getIsAvailable() != null) existing.setIsAvailable(dto.getIsAvailable());
            if (dto.getIsOnline() != null) existing.setIsOnline(dto.getIsOnline());
            if (dto.getLevelOfKorean() != null) existing.setLevelOfKorean(dto.getLevelOfKorean());
            existing.setUpdatedAt(LocalDateTime.now());

            TranslatorProfile saved = repository.save(existing);
            socketServer.getBroadcastOperations().sendEvent("translatorProfileUpdated", saved);
            logger.info("TranslatorProfile updated with id={}", saved.getId());
            return mapToResponse(saved);
        } catch (Exception e) {
            logger.error("Error updating translatorProfile with id={}", id, e);
            throw e;
        }
    }

    // ========== UPDATE THEMES ==========
    public TranslatorProfileResponseDTO updateThemes(Long translatorId, List<Long> themeIds) {
        try {
            logger.info("Updating themes for translatorProfile id={} with themes={}", translatorId, themeIds);

            TranslatorProfile profile = repository.findById(translatorId)
                    .orElseThrow(() -> new ResourceNotFoundException("TranslatorProfile not found with id " + translatorId));

            List<Theme> selectedThemes = themeRepository.findAllById(themeIds);

            if (selectedThemes.isEmpty() && !themeIds.isEmpty()) {
                throw new ResourceNotFoundException("Some theme IDs not found: " + themeIds);
            }

            profile.setThemes(new HashSet<>(selectedThemes));
            profile.setUpdatedAt(LocalDateTime.now());

            TranslatorProfile saved = repository.save(profile);
            socketServer.getBroadcastOperations().sendEvent("translatorThemesUpdated", saved);
            logger.info("Themes updated successfully for translatorProfile id={}", translatorId);

            return mapToResponse(saved);
        } catch (Exception e) {
            logger.error("Error updating themes for translatorProfile id={}", translatorId, e);
            throw e;
        }
    }

    //========== UPDATE AVAILABILITY ==========
    public TranslatorProfileResponseDTO updateAvailability(Long translatorId, Boolean isAvailable) {
        TranslatorProfile profile = repository.findById(translatorId)
                .orElseThrow(() -> new ResourceNotFoundException("TranslatorProfile not found with id " + translatorId));

        profile.setIsAvailable(isAvailable);
        profile.setUpdatedAt(LocalDateTime.now());

        TranslatorProfile saved = repository.save(profile);
        socketServer.getBroadcastOperations().sendEvent("translatorAvailabilityUpdated", saved);
        logger.info("Availability updated for translatorProfile id={}", translatorId);

        return mapToResponse(saved);
    }


    // ========== VERIFY PROFILE ==========
    public TranslatorProfileResponseDTO verifyProfile(Long id) {
        try {
            logger.info("Verifying translatorProfile with id={}", id);
            TranslatorProfile profile = repository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("TranslatorProfile not found with id " + id));

            profile.setIsVerified(true);
            profile.setUpdatedAt(LocalDateTime.now());
            TranslatorProfile saved = repository.save(profile);
            socketServer.getBroadcastOperations().sendEvent("translatorProfileVerified", saved);
            logger.info("TranslatorProfile verified with id={}", saved.getId());
            return mapToResponse(saved);
        } catch (Exception e) {
            logger.error("Error verifying translatorProfile with id={}", id, e);
            throw e;
        }
    }

    // ========== UNVERIFY PROFILE ==========
    public TranslatorProfileResponseDTO unverifyProfile(Long id) {
        try {
            logger.info("Unverifying translatorProfile with id={}", id);
            TranslatorProfile profile = repository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("TranslatorProfile not found with id " + id));

            profile.setIsVerified(false);
            profile.setUpdatedAt(LocalDateTime.now());
            TranslatorProfile saved = repository.save(profile);
            socketServer.getBroadcastOperations().sendEvent("translatorProfileUnverified", saved);
            logger.info("TranslatorProfile unverified with id={}", saved.getId());
            return mapToResponse(saved);
        } catch (Exception e) {
            logger.error("Error unverifying translatorProfile with id={}", id, e);
            throw e;
        }
    }

    // ========== FIND UNVERIFIED PROFILES ==========
    public List<TranslatorProfileResponseDTO> findUnverifiedProfiles() {
        try {
            logger.info("Fetching all unverified translatorProfiles");
            return repository.findAll().stream()
                    .filter(profile -> profile.getIsVerified() == null || !profile.getIsVerified())
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error fetching unverified translatorProfiles", e);
            throw e;
        }
    }

    // ========== PAGINATION + FILTER ==========
    public Page<TranslatorProfileResponseDTO> getAllWithFilterAndPagination(FilterRequest filter, PaginationRequest pagination) {
        try {
            logger.info("Fetching translatorProfiles with filter and pagination");
            Pageable pageable = PageRequest.of(
                    pagination.getPage(),
                    pagination.getSize(),
                    pagination.isAscending() ? Sort.by(pagination.getSortBy()).ascending() : Sort.by(pagination.getSortBy()).descending()
            );

            Page<TranslatorProfile> page = repository.findAll(pageable);

            if (filter != null && filter.hasSearch()) {
                String term = filter.getSearch().toLowerCase();
                List<TranslatorProfile> filtered = page.getContent().stream()
                        .filter(p -> p.getEmail().toLowerCase().contains(term))
                        .toList();
                page = new PageImpl<>(filtered, page.getPageable(), filtered.size());
            }

            return page.map(this::mapToResponse);
        } catch (Exception e) {
            logger.error("Error fetching translatorProfiles with filter and pagination", e);
            throw e;
        }
    }

    // ========== MAPPERS ==========
    private TranslatorProfileResponseDTO mapToResponse(TranslatorProfile entity) {
        TranslatorProfileResponseDTO dto = new TranslatorProfileResponseDTO();
        dto.setId(entity.getId());
        dto.setEmail(entity.getEmail());
        dto.setDateOfBirth(entity.getDateOfBirth());
        dto.setIsAvailable(entity.getIsAvailable());
        dto.setIsOnline(entity.getIsOnline());
        dto.setIsVerified(entity.getIsVerified());
        dto.setLevelOfKorean(entity.getLevelOfKorean());
        dto.setCreatedAtDatetime(entity.getCreatedAt());
        dto.setUpdatedAtDatetime(entity.getUpdatedAt());

        if (entity.getThemes() != null) {
            dto.setThemes(entity.getThemes().stream()
                    .map(Theme::getName)
                    .collect(Collectors.toList()));
        }
        return dto;
    }

    private TranslatorProfile mapToEntity(TranslatorProfileRequestDTO dto) {
        TranslatorProfile entity = new TranslatorProfile();
        entity.setEmail(dto.getEmail());
        entity.setDateOfBirth(dto.getDateOfBirth());
        entity.setIsAvailable(dto.getIsAvailable());
        entity.setIsOnline(dto.getIsOnline());
        entity.setIsVerified(dto.getIsVerified() != null ? dto.getIsVerified() : false);
        entity.setLevelOfKorean(dto.getLevelOfKorean());
        return entity;
    }
}

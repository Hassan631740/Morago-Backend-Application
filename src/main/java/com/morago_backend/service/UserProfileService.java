package com.morago_backend.service;

import com.morago_backend.dto.dtoRequest.UserProfileRequestDTO;
import com.morago_backend.dto.dtoResponse.UserProfileResponseDTO;
import com.morago_backend.entity.UserProfile;
import com.morago_backend.exception.ResourceNotFoundException;
import com.morago_backend.repository.UserProfileRepository;
import com.corundumstudio.socketio.SocketIOServer;
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
public class UserProfileService {

    private static final Logger logger = LoggerFactory.getLogger(UserProfileService.class);

    private final UserProfileRepository repository;
    private final SocketIOServer socketServer;

    // ========== CREATE ==========
    public UserProfileResponseDTO create(UserProfileRequestDTO dto) {
        try {
            logger.info("Creating userProfile");
            UserProfile profile = fromDTO(dto);
            profile.setCreatedAt(LocalDateTime.now());
            profile.setUpdatedAt(LocalDateTime.now());
            UserProfile saved = repository.save(profile);
            socketServer.getBroadcastOperations().sendEvent("userProfileCreated", saved);
            logger.info("UserProfile created with id={}", saved.getId());
            return toDTO(saved);
        } catch (Exception e) {
            logger.error("Error creating userProfile", e);
            throw e;
        }
    }

    // ========== READ ALL ==========
    public List<UserProfileResponseDTO> findAll() {
        try {
            logger.info("Fetching all userProfiles");
            return repository.findAll().stream()
                    .map(this::toDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error fetching all userProfiles", e);
            throw e;
        }
    }

    // ========== READ by ID ==========
    public UserProfileResponseDTO findById(Long id) {
        try {
            logger.info("Fetching userProfile with id={}", id);
            UserProfile profile = repository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("UserProfile not found with id " + id));
            return toDTO(profile);
        } catch (Exception e) {
            logger.error("Error fetching userProfile with id={}", id, e);
            throw e;
        }
    }

    // ========== UPDATE ==========
    public UserProfileResponseDTO update(Long id, UserProfileRequestDTO dto) {
        try {
            logger.info("Updating userProfile with id={}", id);
            UserProfile existing = repository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("UserProfile not found with id " + id));

            existing.setIsFreeCallMade(dto.getIsFreeCallMade());
            existing.setUpdatedAt(LocalDateTime.now());

            UserProfile saved = repository.save(existing);
            socketServer.getBroadcastOperations().sendEvent("userProfileUpdated", saved);
            logger.info("UserProfile updated with id={}", saved.getId());
            return toDTO(saved);
        } catch (Exception e) {
            logger.error("Error updating userProfile with id={}", id, e);
            throw e;
        }
    }

    // ========== DELETE ==========
    public void delete(Long id) {
        try {
            logger.info("Deleting userProfile with id={}", id);
            if (!repository.existsById(id)) {
                logger.warn("UserProfile not found with id={}", id);
                throw new ResourceNotFoundException("UserProfile not found with id " + id);
            }
            repository.deleteById(id);
            socketServer.getBroadcastOperations().sendEvent("userProfileDeleted", id);
            logger.info("UserProfile deleted with id={}", id);
        } catch (Exception e) {
            logger.error("Error deleting userProfile with id={}", id, e);
            throw e;
        }
    }

    // ========== MAPPERS ==========
    private UserProfileResponseDTO toDTO(UserProfile profile) {
        UserProfileResponseDTO dto = new UserProfileResponseDTO();
        dto.setId(profile.getId());
        dto.setIsFreeCallMade(profile.getIsFreeCallMade());
        dto.setCreatedAt(profile.getCreatedAt());
        dto.setUpdatedAt(profile.getUpdatedAt());
        return dto;
    }

    private UserProfile fromDTO(UserProfileRequestDTO dto) {
        UserProfile profile = new UserProfile();
        profile.setIsFreeCallMade(dto.getIsFreeCallMade());
        return profile;
    }
}

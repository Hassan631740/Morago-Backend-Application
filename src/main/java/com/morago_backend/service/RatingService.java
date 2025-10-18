package com.morago_backend.service;

import com.morago_backend.dto.dtoRequest.RatingRequestDTO;
import com.morago_backend.dto.dtoResponse.RatingResponseDTO;
import com.morago_backend.entity.Rating;
import com.morago_backend.exception.ResourceNotFoundException;
import com.morago_backend.repository.RatingRepository;
import com.corundumstudio.socketio.SocketIOServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RatingService {

    private static final Logger logger = LoggerFactory.getLogger(RatingService.class);

    private final RatingRepository ratingRepository;
    private final SocketIOServer socketServer;

    public RatingService(RatingRepository ratingRepository, SocketIOServer socketServer) {
        this.ratingRepository = ratingRepository;
        this.socketServer = socketServer;
    }

    // ========== CREATE ==========
    public RatingResponseDTO create(RatingRequestDTO dto) {
        try {
            logger.info("Creating rating from user {} to user {}", dto.getWhoUserId(), dto.getToWhomUserId());
            Rating entity = mapToEntity(dto);
            Rating saved = ratingRepository.save(entity);
            socketServer.getBroadcastOperations().sendEvent("ratingCreated", saved);
            logger.info("Rating created with id={}", saved.getId());
            return mapToResponse(saved);
        } catch (Exception e) {
            logger.error("Error creating rating from user {} to user {}", dto.getWhoUserId(), dto.getToWhomUserId(), e);
            throw e;
        }
    }

    // ========== READ ALL ==========
    public List<RatingResponseDTO> findAll() {
        try {
            logger.info("Fetching all ratings");
            return ratingRepository.findAll().stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error fetching all ratings", e);
            throw e;
        }
    }

    // ========== READ by ID ==========
    public RatingResponseDTO findById(Long id) {
        try {
            logger.info("Fetching rating with id={}", id);
            Rating entity = ratingRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Rating not found with id " + id));
            return mapToResponse(entity);
        } catch (Exception e) {
            logger.error("Error fetching rating with id={}", id, e);
            throw e;
        }
    }

    // ========== UPDATE ==========
    public RatingResponseDTO update(Long id, RatingRequestDTO dto) {
        try {
            logger.info("Updating rating with id={}", id);
            Rating existing = ratingRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Rating not found with id " + id));

            existing.setWhoUserId(dto.getWhoUserId());
            existing.setToWhomUserId(dto.getToWhomUserId());
            existing.setGrade(dto.getGrade());

            Rating saved = ratingRepository.save(existing);
            socketServer.getBroadcastOperations().sendEvent("ratingUpdated", saved);
            logger.info("Rating updated with id={}", saved.getId());
            return mapToResponse(saved);
        } catch (Exception e) {
            logger.error("Error updating rating with id={}", id, e);
            throw e;
        }
    }

    // ========== DELETE ==========
    public void delete(Long id) {
        try {
            logger.info("Deleting rating with id={}", id);
            if (!ratingRepository.existsById(id)) {
                logger.warn("Rating not found with id={}", id);
                throw new ResourceNotFoundException("Rating not found with id " + id);
            }
            ratingRepository.deleteById(id);
            socketServer.getBroadcastOperations().sendEvent("ratingDeleted", id);
            logger.info("Rating deleted with id={}", id);
        } catch (Exception e) {
            logger.error("Error deleting rating with id={}", id, e);
            throw e;
        }
    }

    // ========== MAPPER ==========
    private RatingResponseDTO mapToResponse(Rating entity) {
        return new RatingResponseDTO(
                entity.getId(),
                entity.getWhoUserId(),
                entity.getToWhomUserId(),
                entity.getGrade(),
                entity.getCreatedAtDatetime(),
                entity.getUpdatedAtDatetime()
        );
    }

    private Rating mapToEntity(RatingRequestDTO dto) {
        Rating entity = new Rating();
        entity.setWhoUserId(dto.getWhoUserId());
        entity.setToWhomUserId(dto.getToWhomUserId());
        entity.setGrade(dto.getGrade());
        return entity;
    }
}

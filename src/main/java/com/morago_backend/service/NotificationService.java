package com.morago_backend.service;

import com.morago_backend.dto.dtoRequest.NotificationRequestDTO;
import com.morago_backend.dto.dtoResponse.NotificationResponseDTO;
import com.morago_backend.entity.Notification;
import com.morago_backend.exception.ResourceNotFoundException;
import com.morago_backend.repository.NotificationRepository;
import com.corundumstudio.socketio.SocketIOServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationRepository repository;
    private final SocketIOServer socketServer;

    public NotificationService(NotificationRepository repository, SocketIOServer socketServer) {
        this.repository = repository;
        this.socketServer = socketServer;
    }

    // ========== CREATE ==========
    public NotificationResponseDTO create(NotificationRequestDTO dto) {
        try {
            logger.info("Creating notification for userId={}", dto.getUserId());
            Notification entity = new Notification();
            entity.setTitle(dto.getTitle());
            entity.setText(dto.getText());
            entity.setDate(dto.getDate());
            entity.setTime(dto.getTime());
            entity.setUserId(dto.getUserId());

            Notification saved = repository.save(entity);

            // ========== SOCKET.IO ==========
            if (saved.getUserId() != null) {
                // Send to user-specific room
                socketServer.getRoomOperations("user_" + saved.getUserId())
                        .sendEvent("notificationCreated", toResponse(saved));
                logger.info("Notification sent to user room user_{}", saved.getUserId());
            } else {
                // Broadcast to all users
                socketServer.getBroadcastOperations().sendEvent("notificationCreated", toResponse(saved));
                logger.info("Notification broadcasted to all users");
            }

            logger.info("Notification created with id={}", saved.getId());
            return toResponse(saved);
        } catch (Exception e) {
            logger.error("Error creating notification for userId={}", dto.getUserId(), e);
            throw e;
        }
    }

    // ========== READ ALL ==========
    public List<NotificationResponseDTO> findAll() {
        try {
            logger.info("Fetching all notifications");
            return repository.findAll().stream()
                    .map(this::toResponse)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error fetching all notifications", e);
            throw e;
        }
    }

    // ========== READ by ID ==========
    public NotificationResponseDTO findById(Long id) {
        try {
            logger.info("Fetching notification with id={}", id);
            Notification entity = repository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id " + id));
            return toResponse(entity);
        } catch (Exception e) {
            logger.error("Error fetching notification with id={}", id, e);
            throw e;
        }
    }

    // ========== UPDATE ==========
    public NotificationResponseDTO update(Long id, NotificationRequestDTO dto) {
        try {
            logger.info("Updating notification with id={}", id);
            Notification existing = repository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id " + id));

            existing.setTitle(dto.getTitle());
            existing.setText(dto.getText());
            existing.setDate(dto.getDate());
            existing.setTime(dto.getTime());
            existing.setUserId(dto.getUserId());

            Notification saved = repository.save(existing);

            // ========== SOCKET.IO ==========
            if (saved.getUserId() != null) {
                socketServer.getRoomOperations("user_" + saved.getUserId())
                        .sendEvent("notificationUpdated", toResponse(saved));
                logger.info("Notification update sent to user room user_{}", saved.getUserId());
            } else {
                socketServer.getBroadcastOperations().sendEvent("notificationUpdated", toResponse(saved));
                logger.info("Notification update broadcasted to all users");
            }

            logger.info("Notification updated with id={}", saved.getId());
            return toResponse(saved);
        } catch (Exception e) {
            logger.error("Error updating notification with id={}", id, e);
            throw e;
        }
    }

    // ========== DELETE ==========
    public void delete(Long id) {
        try {
            logger.info("Deleting notification with id={}", id);
            if (!repository.existsById(id)) {
                logger.warn("Notification not found with id={}", id);
                throw new ResourceNotFoundException("Notification not found with id " + id);
            }
            repository.deleteById(id);

            // Broadcast deletion
            socketServer.getBroadcastOperations().sendEvent("notificationDeleted", id);
            logger.info("Notification deletion broadcasted, id={}", id);
        } catch (Exception e) {
            logger.error("Error deleting notification with id={}", id, e);
            throw e;
        }
    }

    // ========== MAPPER ==========
    private NotificationResponseDTO toResponse(Notification entity) {
        NotificationResponseDTO dto = new NotificationResponseDTO();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setText(entity.getText());
        dto.setDate(entity.getDate());
        dto.setTime(entity.getTime());
        dto.setUserId(entity.getUserId());
        dto.setCreatedAtDatetime(entity.getCreatedAtDatetime());
        dto.setUpdatedAtDatetime(entity.getUpdatedAtDatetime());
        return dto;
    }
}

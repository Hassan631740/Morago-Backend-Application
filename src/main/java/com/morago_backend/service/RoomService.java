package com.morago_backend.service;

import com.morago_backend.dto.dtoRequest.RoomRequestDTO;
import com.morago_backend.dto.dtoResponse.RoomResponseDTO;
import com.morago_backend.entity.Room;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RoomService {

    private static final Logger logger = LoggerFactory.getLogger(RoomService.class);

    private final Map<String, Room> rooms = new HashMap<>();

    // ========== CREATE ROOM ==========
    public RoomResponseDTO createRoom(RoomRequestDTO request) {
        try {
            String roomId = UUID.randomUUID().toString();
            Room room = new Room(roomId, request.getName(), request.getCreatedBy(), new HashSet<>());
            rooms.put(roomId, room);
            logger.info("Room created with id={}", roomId);
            return mapToDTO(room);
        } catch (Exception e) {
            logger.error("Error creating room {}", request.getName(), e);
            throw e;
        }
    }

    // ========== GET ROOM BY ID ==========
    public RoomResponseDTO getRoom(String roomId) {
        try {
            Room room = rooms.get(roomId);
            if (room == null) {
                logger.warn("Room not found with id={}", roomId);
                throw new RuntimeException("Room not found with id " + roomId);
            }
            return mapToDTO(room);
        } catch (Exception e) {
            logger.error("Error fetching room with id={}", roomId, e);
            throw e;
        }
    }

    // ========== GET ALL ROOMS ==========
    public List<RoomResponseDTO> getAllRooms() {
        try {
            return rooms.values().stream()
                    .map(this::mapToDTO)
                    .toList();
        } catch (Exception e) {
            logger.error("Error fetching all rooms", e);
            throw e;
        }
    }

    // ========== UPDATE ROOM ==========
    public RoomResponseDTO updateRoom(String roomId, RoomRequestDTO request) {
        try {
            Room room = rooms.get(roomId);
            if (room == null) {
                logger.warn("Room not found with id={}", roomId);
                throw new RuntimeException("Room not found with id " + roomId);
            }
            room.setName(request.getName());
            room.setCreatedBy(request.getCreatedBy());
            // Optionally update participants if needed
            rooms.put(roomId, room);
            logger.info("Room updated with id={}", roomId);
            return mapToDTO(room);
        } catch (Exception e) {
            logger.error("Error updating room with id={}", roomId, e);
            throw e;
        }
    }

    // ========== DELETE ROOM ==========
    public void deleteRoom(String roomId) {
        try {
            Room removed = rooms.remove(roomId);
            if (removed == null) {
                logger.warn("Room not found with id={}", roomId);
                throw new RuntimeException("Room not found with id " + roomId);
            }
            logger.info("Room deleted with id={}", roomId);
        } catch (Exception e) {
            logger.error("Error deleting room with id={}", roomId, e);
            throw e;
        }
    }

    // ========== MAPPER ==========
    private RoomResponseDTO mapToDTO(Room room) {
        RoomResponseDTO dto = new RoomResponseDTO();
        dto.setId(room.getId());
        dto.setName(room.getName());
        dto.setCreatedBy(room.getCreatedBy());
        dto.setParticipants(room.getParticipants());
        return dto;
    }
}

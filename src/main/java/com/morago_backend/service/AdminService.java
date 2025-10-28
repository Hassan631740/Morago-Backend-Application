package com.morago_backend.service;

import com.morago_backend.dto.dtoRequest.FilterRequest;
import com.morago_backend.dto.dtoRequest.PaginationRequest;
import com.morago_backend.dto.dtoRequest.UserRequestDTO;
import com.morago_backend.dto.dtoResponse.PagedResponse;
import com.morago_backend.dto.dtoResponse.UserResponseDTO;
import com.morago_backend.entity.User;
import com.morago_backend.entity.UserRole;
import com.morago_backend.exception.ResourceNotFoundException;
import com.morago_backend.repository.UserRepository;
import com.corundumstudio.socketio.SocketIOServer;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final SocketIOServer socketServer;
    private final PasswordEncoder passwordEncoder;

    private static final Logger logger = LoggerFactory.getLogger(AdminService.class);

    // ========== CREATE ==========
    public UserResponseDTO create(UserRequestDTO dto) {
        try {
            User user = new User();
            user.setPhone(dto.getPhone());
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
            user.setFirstName(dto.getFirstName());
            user.setLastName(dto.getLastName());
            user.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true);
            user.setRoles(parseRolesFromRequest(dto));

            User saved = userRepository.save(user);
            UserResponseDTO responseDTO = mapToResponse(saved);
            socketServer.getBroadcastOperations().sendEvent("userCreated", responseDTO);
            logger.info("User created successfully with id={}", saved.getId());
            return responseDTO;
        } catch (Exception e) {
            logger.error("Error creating user", e);
            throw e;
        }
    }

    // ========== UPDATE ==========
    public UserResponseDTO update(Long id, UserRequestDTO dto) {
        try {
            User existing = userRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));

            if (dto.getPhone() != null) existing.setPhone(dto.getPhone());
            if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
                existing.setPassword(passwordEncoder.encode(dto.getPassword()));
            }
            if (dto.getFirstName() != null) existing.setFirstName(dto.getFirstName());
            if (dto.getLastName() != null) existing.setLastName(dto.getLastName());
            if (dto.getIsActive() != null) existing.setIsActive(dto.getIsActive());
            if (dto.getRole() != null) existing.setRoles(parseRolesFromRequest(dto));

            User saved = userRepository.save(existing);
            UserResponseDTO responseDTO = mapToResponse(saved);
            socketServer.getBroadcastOperations().sendEvent("userUpdated", responseDTO);
            logger.info("User updated successfully with id={}", saved.getId());
            return responseDTO;
        } catch (Exception e) {
            logger.error("Error updating user with id=" + id, e);
            throw e;
        }
    }

    // ========== DELETE ==========
    public void delete(Long id) {
        try {
            userRepository.deleteById(id);
            socketServer.getBroadcastOperations().sendEvent("userDeleted", id);
            logger.info("User deleted successfully with id={}", id);
        } catch (Exception e) {
            logger.error("Error deleting user with id=" + id, e);
            throw e;
        }
    }

    // ========== GET BY ID ==========
    public Optional<UserResponseDTO> findByIdDTO(Long id) {
        return userRepository.findById(id).map(this::mapToResponse);
    }

    // ========== GET ALL WITH PAGINATION & FILTER ==========
    public PagedResponse<UserResponseDTO> findAllDTOWithPaginationAndFilter(PaginationRequest pagination, FilterRequest filter) {
        Pageable pageable = PageRequest.of(pagination.getPage(), pagination.getSize());
        Page<User> page = applyFilters(userRepository.findAll(pageable), filter);
        List<UserResponseDTO> content = page.getContent().stream()
                .map(this::mapToResponse)
                .toList();
        return new PagedResponse<>(content, page.getNumber(), page.getSize(), page.getTotalElements());
    }

    // ========== BLOCK / ACTIVATE ==========
    public UserResponseDTO blockUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setIsActive(false);
        User saved = userRepository.save(user);
        UserResponseDTO dto = mapToResponse(saved);
        socketServer.getBroadcastOperations().sendEvent("userBlocked", dto);
        return dto;
    }

    public UserResponseDTO activateUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setIsActive(true);
        User saved = userRepository.save(user);
        UserResponseDTO dto = mapToResponse(saved);
        socketServer.getBroadcastOperations().sendEvent("userActivated", dto);
        return dto;
    }

    // ========== HELPER METHODS ==========
    private Set<UserRole> parseRolesFromRequest(UserRequestDTO dto) {
        if (dto.getRole() != null && !dto.getRole().isBlank()) {
            return Set.of(UserRole.valueOf(dto.getRole()));
        }
        return Collections.emptySet();
    }

    private UserResponseDTO mapToResponse(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setPhone(user.getPhone());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setBalance(user.getBalance());
        dto.setRatings(user.getRatings());
        dto.setTotalRatings(user.getTotalRatings());
        dto.setIsActive(user.getIsActive());
        dto.setRoles(user.getRoles().stream().map(Enum::name).collect(Collectors.toSet()));
        dto.setCreatedAtDatetime(user.getCreatedAt());
        dto.setUpdatedAtDatetime(user.getUpdatedAt());
        return dto;
    }

    private Page<User> applyFilters(Page<User> page, FilterRequest filter) {
        if (filter == null) return page;

        List<User> filtered = page.getContent();

        if (filter.hasSearch()) {
            String term = filter.getSearch().toLowerCase();
            filtered = filtered.stream()
                    .filter(u -> u.getPhone().toLowerCase().contains(term) ||
                            (u.getFirstName() != null && u.getFirstName().toLowerCase().contains(term)) ||
                            (u.getLastName() != null && u.getLastName().toLowerCase().contains(term)))
                    .toList();
        }

        if (filter.hasFilters()) {
            if (filter.getFilters().containsKey("role")) {
                String roleFilter = filter.getFilters().get("role").toString();
                filtered = filtered.stream()
                        .filter(u -> u.getRoles().stream()
                                .anyMatch(r -> r.name().equalsIgnoreCase(roleFilter)))
                        .toList();
            }
            if (filter.getFilters().containsKey("active")) {
                boolean activeFilter = Boolean.parseBoolean(filter.getFilters().get("active").toString());
                filtered = filtered.stream()
                        .filter(u -> Boolean.TRUE.equals(u.getIsActive()) == activeFilter)
                        .toList();
            }
        }

        return new PageImpl<>(filtered, page.getPageable(), page.getTotalElements());
    }
}

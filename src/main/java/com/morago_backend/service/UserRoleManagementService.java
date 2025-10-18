package com.morago_backend.service;

import com.morago_backend.dto.dtoResponse.UserRoleResponseDTO;
import com.morago_backend.entity.UserRole;
import com.morago_backend.entity.User;
import com.morago_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserRoleManagementService {

    private static final Logger logger = LoggerFactory.getLogger(UserRoleManagementService.class);

    private final UserRepository userRepository;

    // ========== MAPPER ==========
    private UserRoleResponseDTO mapToDTO(User user) {
        UserRoleResponseDTO dto = new UserRoleResponseDTO();
        dto.setId(user.getId());
        dto.setPhone(user.getPhone());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setRoles(user.getRoles().stream().map(Enum::name).collect(Collectors.toSet()));
        return dto;
    }

    // ========== GET USER ROLES ==========
    public Set<String> getUserRoles(Long userId) {
        try {
            logger.info("Fetching roles for userId={}", userId);
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
            return user.getRoles().stream().map(Enum::name).collect(Collectors.toSet());
        } catch (Exception e) {
            logger.error("Error fetching roles for userId={}", userId, e);
            throw e;
        }
    }

    // ========== CHECK ROLE ==========
    public boolean userHasRole(Long userId, String roleName) {
        try {
            logger.info("Checking if userId={} has role={}", userId, roleName);
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

            boolean hasRole = user.getRoles().stream()
                    .anyMatch(role -> role.name().equalsIgnoreCase(roleName));
            logger.info("UserId={} has role={} : {}", userId, roleName, hasRole);
            return hasRole;
        } catch (Exception e) {
            logger.error("Error checking role={} for userId={}", roleName, userId, e);
            throw e;
        }
    }

    // ========== GET USERS BY ROLE ==========
    public List<UserRoleResponseDTO> getUsersByRole(String roleName) {
        try {
            logger.info("Fetching users with role={}", roleName);
            UserRole role = UserRole.valueOf(roleName);
            List<UserRoleResponseDTO> users = userRepository.findByRoles(role).stream()
                    .map(this::mapToDTO)
                    .collect(Collectors.toList());
            logger.info("Found {} users with role={}", users.size(), roleName);
            return users;
        } catch (Exception e) {
            logger.error("Error fetching users with role={}", roleName, e);
            throw e;
        }
    }

    // ========== GET ALL CLIENTS ==========
    public List<UserRoleResponseDTO> getAllClients() {
        return getUsersByRole("CLIENT");
    }

    // ========== GET ALL INTERPRETERS ==========
    public List<UserRoleResponseDTO> getAllInterpreters() {
        return getUsersByRole("INTERPRETER");
    }
}
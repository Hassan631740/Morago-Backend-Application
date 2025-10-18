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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SocketIOServer socketServer;

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    protected Page<User> applyFilters(Page<User> page, FilterRequest filter) {
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

    //=== Helper: get current authenticated user ===//
    private User getCurrentUserEntity() {
        try {
            String phone = SecurityContextHolder.getContext().getAuthentication().getName();
            return userRepository.findByPhone(phone)
                    .orElseThrow(() -> new ResourceNotFoundException("Current user not found"));
        } catch (Exception e) {
            logger.error("Error fetching current authenticated user", e);
            throw e;
        }
    }

    //=== Get current user's profile ===//
    public UserResponseDTO getCurrentUserProfile() {
        try {
            User user = getCurrentUserEntity();
            logger.info("Fetched profile for user id={}", user.getId());
            return mapToResponse(user);
        } catch (Exception e) {
            logger.error("Error getting current user profile", e);
            throw e;
        }
    }

    //=== Update current user's profile ===//
    public UserResponseDTO updateCurrentUser(UserRequestDTO request) {
        try {
            User user = getCurrentUserEntity();

            if (request.getFirstName() != null) user.setFirstName(request.getFirstName());
            if (request.getLastName() != null) user.setLastName(request.getLastName());
            if (request.getPassword() != null && !request.getPassword().isBlank()) {
                user.setPassword(passwordEncoder.encode(request.getPassword()));
            }

            User saved = userRepository.save(user);
            UserResponseDTO dto = mapToResponse(saved);
            socketServer.getBroadcastOperations().sendEvent("userUpdated", dto);
            logger.info("Updated profile for user id={}", saved.getId());
            return dto;
        } catch (Exception e) {
            logger.error("Error updating current user profile", e);
            throw e;
        }
    }

    //=== Deposit money (Client only) ===//
    public UserResponseDTO deposit(BigDecimal amount) {
        try {
            User user = getCurrentUserEntity();
            if (!user.getRoles().contains(UserRole.CLIENT)) {
                throw new RuntimeException("Only clients can deposit");
            }

            if (amount.compareTo(BigDecimal.ZERO) <= 0) throw new IllegalArgumentException("Amount must be positive");

            user.setBalance(user.getBalance() == null ? amount : user.getBalance().add(amount));
            User saved = userRepository.save(user);
            socketServer.getBroadcastOperations().sendEvent("userDeposited", saved);
            return mapToResponse(saved);
        } catch (Exception e) {
            logger.error("Error depositing", e);
            throw e;
        }
    }

    //=== Get current balance ===//
    public BigDecimal getBalance() {
        try {
            User user = getCurrentUserEntity();
            BigDecimal balance = user.getBalance() != null ? user.getBalance() : BigDecimal.ZERO;
            logger.info("Fetched balance={} for user id={}", balance, user.getId());
            return balance;
        } catch (Exception e) {
            logger.error("Error getting user balance", e);
            throw e;
        }
    }

    //=== Get all users with pagination & filter (for admin only usually) ===//
    public PagedResponse<UserResponseDTO> findAllDTOWithPaginationAndFilter(PaginationRequest pagination, FilterRequest filter) {
        try {
            logger.info("Fetching users with pagination={} filter={}", pagination, filter);
            Pageable pageable = PageRequest.of(pagination.getPage(), pagination.getSize());
            Page<User> page = applyFilters(userRepository.findAll(pageable), filter);
            List<UserResponseDTO> content = page.getContent().stream()
                    .map(this::mapToResponse)
                    .toList();
            return new PagedResponse<>(content, page.getNumber(), page.getSize(), page.getTotalElements());
        } catch (Exception e) {
            logger.error("Error fetching users with pagination & filter", e);
            throw e;
        }
    }

    //=== Map User entity to Response DTO ===//
    private UserResponseDTO mapToResponse(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setPhone(user.getPhone());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setBalance(user.getBalance());
        dto.setRoles(user.getRoles().stream().map(Enum::name).collect(java.util.stream.Collectors.toSet()));
        return dto;
    }
}

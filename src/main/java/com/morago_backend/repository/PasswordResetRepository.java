package com.morago_backend.repository;

import com.morago_backend.entity.PasswordReset;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetRepository extends JpaRepository<PasswordReset, Long> {
    Optional<PasswordReset> findTopByPhoneOrderByCreatedAtDatetimeDesc(String phone);
}

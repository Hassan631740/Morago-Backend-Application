package com.morago_backend.repository;

import com.morago_backend.entity.TranslatorProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TranslatorProfileRepository extends JpaRepository<TranslatorProfile, Long> {
    Optional<TranslatorProfile> findByUserPhone(String phone);
}


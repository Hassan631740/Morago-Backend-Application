package com.morago_backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "password_resets")
public class PasswordReset extends BaseEntity {

    @Column(name = "phone_varchar100", length = 100)
    private String phone;

    @Column(name = "reset_code_int")
    private Integer resetCode;

    @Column(name = "is_verified", nullable = false)
    private Boolean isVerified = false;

    @Column(name = "verification_token", length = 255)
    private String verificationToken;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

}
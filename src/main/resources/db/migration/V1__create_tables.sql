-- V1: Create core tables

CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    phone VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(200),
    last_name VARCHAR(200),
    balance DECIMAL(10,2) DEFAULT 0.00,
    ratings DECIMAL(15,2) DEFAULT 0.00,
    total_ratings INT DEFAULT 0,
    created_at TIMESTAMP NULL,
    updated_at TIMESTAMP NULL,
    is_active BOOLEAN DEFAULT TRUE,
    is_debtor BOOLEAN DEFAULT FALSE,
    image_id BIGINT,
    translator_profile_id BIGINT,
    user_profile_id BIGINT
);

-- Element collection table for enum roles
CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL,
    role VARCHAR(50) NOT NULL,
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT uq_user_role UNIQUE (user_id, role)
);


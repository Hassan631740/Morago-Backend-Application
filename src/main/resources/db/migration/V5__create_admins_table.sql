-- V5__create_admins_table.sql
-- Create admins table to track administrator profiles

CREATE TABLE IF NOT EXISTS admins (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    admin_level VARCHAR(50),
    department VARCHAR(100),
    permissions TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    last_login TIMESTAMP NULL,
    login_count INT DEFAULT 0,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_admin_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Create indexes for better query performance
CREATE INDEX idx_admins_user_id ON admins(user_id);
CREATE INDEX idx_admins_admin_level ON admins(admin_level);
CREATE INDEX idx_admins_department ON admins(department);
CREATE INDEX idx_admins_is_active ON admins(is_active);


-- V6: Create transactions table for unified financial operations tracking

CREATE TABLE IF NOT EXISTS transactions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    transaction_type VARCHAR(50) NOT NULL,
    amount DECIMAL(12,2) NOT NULL,
    balance_before DECIMAL(12,2),
    balance_after DECIMAL(12,2),
    status VARCHAR(50),
    description VARCHAR(500),
    
    -- References to related entities
    deposit_id BIGINT,
    withdrawal_id BIGINT,
    call_record_id BIGINT,
    debtor_id BIGINT,
    
    -- Additional metadata
    account_holder VARCHAR(200),
    bank_name VARCHAR(200),
    account_number VARCHAR(200),
    notes VARCHAR(1000),
    
    -- Audit fields (inherited from BaseEntity pattern)
    created_at_datetime TIMESTAMP NULL,
    updated_at_datetime TIMESTAMP NULL,
    
    -- Foreign key constraint
    CONSTRAINT fk_transactions_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    
    -- Indexes for better query performance
    INDEX idx_user_id (user_id),
    INDEX idx_transaction_type (transaction_type),
    INDEX idx_status (status),
    INDEX idx_created_at_datetime (created_at_datetime)
);


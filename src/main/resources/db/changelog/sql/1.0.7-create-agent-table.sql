--liquibase formatted sql
--changeset ibmk:order-014
--comment: Create agent table safely

-- Create table if it doesn't exist
CREATE TABLE IF NOT EXISTS agent (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    version INT,
    status VARCHAR(255),
    last_update DATETIME,
    creation_time DATETIME,
    name VARCHAR(255),
    phone_number VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    description VARCHAR(255),
    deleted BOOLEAN NOT NULL DEFAULT FALSE
);

-- Create idx_agent_phone_number safely
SET @idx_exists := (
    SELECT COUNT(*)
    FROM INFORMATION_SCHEMA.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'agent'
      AND INDEX_NAME = 'idx_agent_phone_number'
);

SET @sql := IF(@idx_exists = 0,
               'CREATE INDEX idx_agent_phone_number ON agent(phone_number)',
               'SELECT ''Index idx_agent_phone_number already exists'' AS message');

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Create idx_agent_name safely
SET @idx_exists := (
    SELECT COUNT(*)
    FROM INFORMATION_SCHEMA.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'agent'
      AND INDEX_NAME = 'idx_agent_name'
);

SET @sql := IF(@idx_exists = 0,
               'CREATE INDEX idx_agent_name ON agent(name)',
               'SELECT ''Index idx_agent_name already exists'' AS message');

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Insert default row only if it doesn't exist
INSERT INTO agent (
    version,
    status,
    last_update,
    creation_time,
    name,
    phone_number,
    email,
    description,
    deleted
)
SELECT 1, 'ACTIVE', NOW(), NOW(), 'Default Agent', '0500000000', 'default.agent@example.com', 'System default delivery agent', FALSE
WHERE NOT EXISTS (
    SELECT 1 FROM agent WHERE phone_number = '0500000000'
);

--rollback DROP TABLE IF EXISTS agent;
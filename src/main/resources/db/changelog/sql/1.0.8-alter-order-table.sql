--liquibase formatted sql
--changeset ibmk:order-015
--comment: Alter orders table to use agent_id safely

-- Add 'agent_id' column safely (before dropping delivery_agent to preserve data)
SET @col_exists := (
    SELECT COUNT(*)
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'orders'
      AND COLUMN_NAME = 'agent_id'
);

SET @sql := IF(@col_exists = 0,
               'ALTER TABLE orders ADD COLUMN agent_id BIGINT',
               'SELECT ''Column agent_id already exists'' AS message');

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Update existing orders to use Default Agent
UPDATE orders o
SET agent_id = (
    SELECT a.id
    FROM agent a
    WHERE a.name = 'Default Agent'
    LIMIT 1
)
WHERE agent_id IS NULL;

-- Add foreign key constraint safely
SET @fk_exists := (
    SELECT COUNT(*)
    FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'orders'
      AND CONSTRAINT_NAME = 'fk_orders_agent'
);

SET @sql := IF(@fk_exists = 0,
               'ALTER TABLE orders ADD CONSTRAINT fk_orders_agent FOREIGN KEY (agent_id) REFERENCES agent(id)',
               'SELECT ''Foreign key fk_orders_agent already exists'' AS message');

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Create index safely
SET @idx_exists := (
    SELECT COUNT(*)
    FROM INFORMATION_SCHEMA.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'orders'
      AND INDEX_NAME = 'idx_orders_agent_id'
);

SET @sql := IF(@idx_exists = 0,
               'CREATE INDEX idx_orders_agent_id ON orders(agent_id)',
               'SELECT ''Index idx_orders_agent_id already exists'' AS message');

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Drop column 'delivery_agent' safely (after migrating data)
SET @col_exists := (
    SELECT COUNT(*)
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'orders'
      AND COLUMN_NAME = 'delivery_agent'
);

SET @sql := IF(@col_exists > 0,
               'ALTER TABLE orders DROP COLUMN delivery_agent',
               'SELECT ''Column delivery_agent does not exist'' AS message');

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

--rollback ALTER TABLE orders DROP FOREIGN KEY IF EXISTS fk_orders_agent;
--rollback DROP INDEX IF EXISTS idx_orders_agent_id ON orders;
--rollback ALTER TABLE orders ADD COLUMN delivery_agent VARCHAR(255);
--rollback ALTER TABLE orders DROP COLUMN agent_id;
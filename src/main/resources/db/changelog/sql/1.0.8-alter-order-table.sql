--changeset ibmk:order-015
--comment: Alter orders table to use agent_id safely

-- Drop column 'delivery_agent' safely
SET @col_exists := (
    SELECT COUNT(*)
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'orders'
      AND COLUMN_NAME = 'delivery_agent'
);
SET @sql := IF(@col_exists > 0,
               'ALTER TABLE orders DROP COLUMN delivery_agent;',
               NULL);
IF @sql IS NOT NULL THEN
    PREPARE stmt FROM @sql;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;
END IF;

-- Add 'agent_id' column safely
SET @col_exists := (
    SELECT COUNT(*)
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'orders'
      AND COLUMN_NAME = 'agent_id'
);
SET @sql := IF(@col_exists = 0,
               'ALTER TABLE orders ADD COLUMN agent_id BIGINT;',
               NULL);
IF @sql IS NOT NULL THEN
    PREPARE stmt FROM @sql;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;
END IF;

-- Update existing orders to use Default Agent
UPDATE orders o
SET agent_id = (
    SELECT a.id
    FROM agent a
    WHERE a.name = 'Default Agent'
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
               'ALTER TABLE orders ADD CONSTRAINT fk_orders_agent FOREIGN KEY (agent_id) REFERENCES agent(id);',
               NULL);
IF @sql IS NOT NULL THEN
    PREPARE stmt FROM @sql;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;
END IF;

-- Create index safely
SET @idx_exists := (
    SELECT COUNT(*)
    FROM INFORMATION_SCHEMA.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'orders'
      AND INDEX_NAME = 'idx_orders_agent_id'
);
SET @sql := IF(@idx_exists = 0,
               'CREATE INDEX idx_orders_agent_id ON orders(agent_id);',
               NULL);
IF @sql IS NOT NULL THEN
    PREPARE stmt FROM @sql;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;
END IF;

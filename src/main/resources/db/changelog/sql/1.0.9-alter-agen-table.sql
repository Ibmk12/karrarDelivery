--liquibase formatted sql
--changeset ibmk:order-016


-- Check if column exists before renaming
SET @col_exists := (
    SELECT COUNT(*)
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'agent'
      AND COLUMN_NAME = 'last_update'
);

SET @sql := IF(@col_exists > 0,
               'ALTER TABLE agent CHANGE COLUMN last_update last_updated DATETIME',
               'SELECT ''Column last_update does not exist'' AS message');

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
--liquibase formatted sql

--changeset ibmk:001
--comment: Create truck table and index on plate number

CREATE TABLE truck (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    plate_number VARCHAR(255) NOT NULL,
    registration_type VARCHAR(255) NOT NULL
);

--changeset ibmk:002
--comment: Create index on plate_number
CREATE INDEX idx_plate_number ON truck (plate_number);

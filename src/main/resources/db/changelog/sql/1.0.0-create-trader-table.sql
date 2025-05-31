--liquibase formatted sql

--changeset ibmk:trader-001
--comment: Create trader table

CREATE TABLE trader (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    version INT,
    status VARCHAR(255),
    last_update DATETIME,
    creation_time DATETIME,

    name VARCHAR(255),
    phone_number VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    description VARCHAR(255),
    deleted BOOLEAN NOT NULL
);

--changeset ibmk:trader-002
--comment: Create index on phone_number
CREATE INDEX idx_trader_phone_number ON trader (phone_number);

--changeset ibmk:trader-003
--comment: Create index on name
CREATE INDEX idx_trader_name ON trader (name);

--liquibase formatted sql

--changeset ibmk:order-008
--comment: Added blacklist table

--liquibase formatted sql

CREATE TABLE blacklisted_tokens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    access_token VARCHAR(512) UNIQUE,
    refresh_token VARCHAR(512) UNIQUE,
    expiry_date DATETIME NOT NULL
);



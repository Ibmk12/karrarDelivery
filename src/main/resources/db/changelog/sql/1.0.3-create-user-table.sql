--liquibase formatted sql

--changeset ibmk:order-006
--comment: Added user table

CREATE TABLE user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(255),
    middle_name VARCHAR(255),
    last_name VARCHAR(255),
    phone VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(255),
    password VARCHAR(255),
    enabled BIT,
    deleted BIT,
    role VARCHAR(100)
);

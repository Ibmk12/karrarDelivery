--liquibase formatted sql

--changeset ibmk:order-007
--comment: Add admin user Admin 1234

--liquibase formatted sql

INSERT INTO user (
    first_name, middle_name, last_name,
    phone, email, password,
    enabled, deleted, role
) VALUES (
    'Admin', '', 'User',
    '971500000000', 'admin@example.com',
    '$2a$10$19ean/TsbPV6/wXjt9vHXenhQ6k/G7z1aCsK4c7mSQZfc3aT8y9Hq',
    1, 0, 'ADMIN'
);


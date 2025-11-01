--liquibase formatted sql

--changeset ibmk:order-007
--comment: Create agent table

CREATE TABLE agent (
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

--changeset ibmk:agent-002
--comment: Create index on phone_number
CREATE INDEX idx_agent_phone_number ON agent (phone_number);

--changeset ibmk:agent-003
--comment: Create index on name
CREATE INDEX idx_agent_name ON agent (name);

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
) VALUES (
    1,
    'ACTIVE',
    NOW(),
    NOW(),
    'Default Agent',
    '0500000000',
    'default.agent@example.com',
    'System default delivery agent',
    FALSE
);
--liquibase formatted sql

--changeset ibmk:order-008
--comment: Create agent table

ALTER TABLE orders
DROP COLUMN delivery_agent;

ALTER TABLE orders
ADD COLUMN agent_id BIGINT;

UPDATE orders o
SET agent_id = (
    SELECT a.id
    FROM agent a
    WHERE a.name = 'Default Agent'
);

ALTER TABLE orders
ADD CONSTRAINT fk_orders_agent
FOREIGN KEY (agent_id) REFERENCES agent(id);

CREATE INDEX idx_orders_agent_id ON orders (agent_id);

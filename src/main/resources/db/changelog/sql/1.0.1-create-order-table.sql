--liquibase formatted sql

--changeset ibmk:order-001
--comment: Create orders table

CREATE TABLE orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_date DATETIME,
    invoice_no VARCHAR(255),
    delivery_status VARCHAR(255) NOT NULL,
    trader_id BIGINT,
    emirate VARCHAR(255) NOT NULL,
    delivery_agent VARCHAR(255),
    delivery_date DATETIME,
    address VARCHAR(255),
    longitude VARCHAR(255),
    latitude VARCHAR(255),
    total_amount DOUBLE,
    trader_amount DOUBLE,
    delivery_amount DOUBLE,
    agent_amount DOUBLE,
    net_company_amount DOUBLE,
    customer_phone_no VARCHAR(255),
    comment VARCHAR(255),
    last_updated DATETIME NOT NULL,
    CONSTRAINT fk_orders_trader FOREIGN KEY (trader_id) REFERENCES trader(id)
);

--changeset ibmk:order-002
--comment: Create index on invoice_no
CREATE INDEX idx_orders_invoice_no ON orders (invoice_no);

--changeset ibmk:order-003
--comment: Create index on order_date
CREATE INDEX idx_orders_order_date ON orders (order_date);

--changeset ibmk:order-004
--comment: Create index on delivery_status
CREATE INDEX idx_orders_delivery_status ON orders (delivery_status);

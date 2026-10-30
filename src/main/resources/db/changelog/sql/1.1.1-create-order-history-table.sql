--liquibase formatted sql

--changeset ibmk:order-history-001
--comment: Order history (audit trail) - who changed what on an order and when

CREATE TABLE IF NOT EXISTS order_history (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id    BIGINT       NOT NULL,
    invoice_no  VARCHAR(255),
    action      VARCHAR(40)  NOT NULL,
    field_name  VARCHAR(60),
    old_value   VARCHAR(512),
    new_value   VARCHAR(512),
    changed_by  VARCHAR(100),
    changed_at  DATETIME     NOT NULL
);

CREATE INDEX idx_order_history_order_id ON order_history(order_id);

--rollback DROP TABLE order_history;

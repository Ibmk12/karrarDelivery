--liquibase formatted sql

--changeset ibmk:saved-order-view-001
--comment: Saved filter views on the Orders page, one list per user

CREATE TABLE IF NOT EXISTS saved_order_view (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_phone  VARCHAR(100) NOT NULL,
    name        VARCHAR(60)  NOT NULL,
    filters     TEXT         NOT NULL,
    created_at  DATETIME     NOT NULL
);

CREATE INDEX idx_saved_order_view_user ON saved_order_view(user_phone);

--rollback DROP TABLE saved_order_view;

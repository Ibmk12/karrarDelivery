--liquibase formatted sql

--changeset ibmk:order-009
--comment: Add unique constraint on invoice_no

--liquibase formatted sql

ALTER TABLE orders ADD CONSTRAINT uc_orders_invoice_no UNIQUE (invoice_no);




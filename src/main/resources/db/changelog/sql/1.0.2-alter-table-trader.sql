--liquibase formatted sql

--changeset ibmk:order-005
--comment: Alter traders table, add code column

ALTER TABLE trader ADD code VARCHAR(255);


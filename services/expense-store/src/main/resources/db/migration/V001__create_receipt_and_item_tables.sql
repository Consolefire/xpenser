-- V001__create_initial_tables.sql

-- This migration script creates the initial set of tables for the xpenser-store service.
-- It includes tables for taxes, tax_details, payments, receipts, and items,
-- with appropriate columns, data types, constraints, and relationships for PostgreSQL.

---
-- Table: taxes
---
CREATE TABLE IF NOT EXISTS taxes (
    id BIGSERIAL PRIMARY KEY,
    total NUMERIC(19, 4) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

---
-- Table: tax_details
---
CREATE TABLE IF NOT EXISTS tax_details (
    id BIGSERIAL PRIMARY KEY,
    type VARCHAR(120) NOT NULL,
    tax_unit TEXT,
    amount NUMERIC(19, 4) NOT NULL,
    taxable_amount NUMERIC(19, 4) NOT NULL,
    item_category VARCHAR(120) NOT NULL,
    tax_id BIGINT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    CONSTRAINT fk_tax_details_tax FOREIGN KEY (tax_id) REFERENCES taxes (id)
);

---
-- Table: payments
---
CREATE TABLE IF NOT EXISTS payments (
    id BIGSERIAL PRIMARY KEY,
    amount NUMERIC(19, 4) NOT NULL,
    mode VARCHAR(120) NOT NULL,
    currency VARCHAR(24) NOT NULL,
    tax_id BIGINT UNIQUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    CONSTRAINT fk_payments_tax FOREIGN KEY (tax_id) REFERENCES taxes (id)
);

---
-- Table: receipts
---
CREATE TABLE IF NOT EXISTS receipts (
    id BIGSERIAL PRIMARY KEY,
    identifier VARCHAR(120) NOT NULL,
    metadata TEXT,
    market_place_code VARCHAR(120) NOT NULL,
    store_code VARCHAR(120) NOT NULL,
    generated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    total NUMERIC(19, 4) NOT NULL,
    currency VARCHAR(24) NOT NULL,
    payment_id BIGINT UNIQUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    CONSTRAINT uk_market_store_identifier UNIQUE (identifier, market_place_code, store_code),
    CONSTRAINT fk_receipts_payment FOREIGN KEY (payment_id) REFERENCES payments (id)
);

---
-- Table: items
---
CREATE TABLE IF NOT EXISTS items (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(120) NOT NULL,
    title VARCHAR(255) NOT NULL,
    quantity INTEGER NOT NULL,
    price NUMERIC(19, 4) NOT NULL,
    category_code VARCHAR(120) NOT NULL,
    unit_value TEXT,
    receipt_id BIGINT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    CONSTRAINT fk_items_receipt FOREIGN KEY (receipt_id) REFERENCES receipts (id)
);

-- =============================================
-- BIS — Billing & Inventory System
-- V1 — Initial Schema
-- =============================================

-- Users
CREATE TABLE IF NOT EXISTS s_usr (
    user_id         BIGSERIAL PRIMARY KEY,
    user_name       VARCHAR(50)  NOT NULL,
    ph_no           VARCHAR(15),
    user_address    VARCHAR(200),
    userdob         DATE,
    user_blood_type VARCHAR(2)   NOT NULL,
    user_admin      BOOLEAN      NOT NULL DEFAULT FALSE,
    user_pass       VARCHAR(255) NOT NULL
);

-- Products
CREATE TABLE IF NOT EXISTS s_product (
    product_id       BIGSERIAL PRIMARY KEY,
    product_name     VARCHAR(255) NOT NULL,
    discription      VARCHAR(255),
    product_price    DOUBLE PRECISION NOT NULL,
    product_quantity BIGINT           NOT NULL DEFAULT 0,
    created_at       TIMESTAMP        NOT NULL,
    updated_at       TIMESTAMP        NOT NULL
);

-- Services
CREATE TABLE IF NOT EXISTS s_service (
    service_id    BIGSERIAL PRIMARY KEY,
    service_name  VARCHAR(255) NOT NULL,
    service_price DOUBLE PRECISION NOT NULL,
    created_at    TIMESTAMP NOT NULL,
    updated_at    TIMESTAMP NOT NULL
);

-- Login
CREATE TABLE IF NOT EXISTS s_login (
    login_id       BIGSERIAL PRIMARY KEY,
    login_usrid    BIGINT    NOT NULL,
    login_datetime TIMESTAMP,
    login_flag     CHAR(1)   NOT NULL
);

-- Bills
CREATE TABLE IF NOT EXISTS s_bill (
    bill_id          SERIAL PRIMARY KEY,
    bill_date        TIMESTAMP,
    bill_amount      DOUBLE PRECISION NOT NULL,
    bill_dues        DOUBLE PRECISION DEFAULT 0.0,
    is_due           BOOLEAN          DEFAULT FALSE,
    discount_percent DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    gst_percent      DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    payment_mode     VARCHAR(10),
    client_name      VARCHAR(50),
    client_ph_no     VARCHAR(15),
    email_id         VARCHAR(255),
    notes            VARCHAR(255),
    staff_id         BIGINT NOT NULL REFERENCES s_usr(user_id)
);

-- Bill Items
CREATE TABLE IF NOT EXISTS s_bill_item (
    bill_item_id SERIAL PRIMARY KEY,
    quantity     INTEGER          NOT NULL,
    subtotal     DOUBLE PRECISION NOT NULL,
    bill_id      INTEGER REFERENCES s_bill(bill_id),
    product_id   BIGINT  REFERENCES s_product(product_id),
    service_id   BIGINT  REFERENCES s_service(service_id)
);

-- Dues
CREATE TABLE IF NOT EXISTS s_dues (
    dues_id      SERIAL PRIMARY KEY,
    client_name  VARCHAR(50)      NOT NULL,
    client_phone VARCHAR(15),
    total_amount DOUBLE PRECISION NOT NULL,
    paid_amount  DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    due_amount   DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    duedate      DATE,
    status       VARCHAR(10),
    notes        VARCHAR(255),
    bill_id      INTEGER REFERENCES s_bill(bill_id)
);

-- Drop old product_model table if it exists (was created by mistake)
DROP TABLE IF EXISTS product_model;
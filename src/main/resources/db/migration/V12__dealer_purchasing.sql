CREATE TABLE IF NOT EXISTS s_dealer (
    dealer_id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(10) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT dealer_phone_format CHECK (phone ~ '^[0-9]{10}$')
);

CREATE INDEX IF NOT EXISTS idx_dealer_name ON s_dealer (LOWER(name));
CREATE INDEX IF NOT EXISTS idx_dealer_phone ON s_dealer (phone);

CREATE TABLE IF NOT EXISTS s_dealer_product (
    dealer_id BIGINT NOT NULL REFERENCES s_dealer(dealer_id) ON DELETE CASCADE,
    product_id BIGINT NOT NULL REFERENCES s_product(product_id),
    PRIMARY KEY (dealer_id, product_id)
);

CREATE TABLE IF NOT EXISTS s_dealer_bill (
    bill_id BIGSERIAL PRIMARY KEY,
    dealer_id BIGINT NOT NULL REFERENCES s_dealer(dealer_id),
    invoice_number VARCHAR(100) NOT NULL,
    bill_date DATE NOT NULL,
    total_amount NUMERIC(14, 2) NOT NULL CHECK (total_amount > 0),
    paid_amount NUMERIC(14, 2) NOT NULL DEFAULT 0 CHECK (paid_amount >= 0),
    due_amount NUMERIC(14, 2) NOT NULL CHECK (due_amount >= 0),
    notes VARCHAR(1000),
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT dealer_bill_payment_bounds CHECK (paid_amount <= total_amount),
    CONSTRAINT dealer_bill_balance CHECK (due_amount = total_amount - paid_amount)
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_dealer_bill_invoice_unique
    ON s_dealer_bill (dealer_id, LOWER(invoice_number));

CREATE INDEX IF NOT EXISTS idx_dealer_bill_dealer_date
    ON s_dealer_bill (dealer_id, bill_date DESC);

CREATE TABLE IF NOT EXISTS s_dealer_bill_item (
    bill_item_id BIGSERIAL PRIMARY KEY,
    bill_id BIGINT NOT NULL REFERENCES s_dealer_bill(bill_id) ON DELETE CASCADE,
    product_id BIGINT NOT NULL REFERENCES s_product(product_id),
    product_name VARCHAR(255) NOT NULL,
    quantity INTEGER NOT NULL CHECK (quantity > 0),
    unit_price NUMERIC(14, 2) NOT NULL CHECK (unit_price >= 0),
    subtotal NUMERIC(14, 2) NOT NULL CHECK (subtotal >= 0)
);

CREATE TABLE IF NOT EXISTS s_dealer_payment (
    payment_id BIGSERIAL PRIMARY KEY,
    bill_id BIGINT NOT NULL REFERENCES s_dealer_bill(bill_id) ON DELETE CASCADE,
    amount NUMERIC(14, 2) NOT NULL CHECK (amount > 0),
    payment_date DATE NOT NULL,
    method VARCHAR(30) NOT NULL,
    notes VARCHAR(1000),
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_dealer_payment_bill_date
    ON s_dealer_payment (bill_id, payment_date);

CREATE TABLE IF NOT EXISTS s_dealer_delivery (
    delivery_id BIGSERIAL PRIMARY KEY,
    dealer_id BIGINT NOT NULL REFERENCES s_dealer(dealer_id),
    bill_id BIGINT REFERENCES s_dealer_bill(bill_id),
    delivery_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL CHECK (status IN ('DELIVERED', 'PARTIAL', 'PENDING')),
    notes VARCHAR(1000),
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_dealer_delivery_dealer_date
    ON s_dealer_delivery (dealer_id, delivery_date DESC);

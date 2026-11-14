CREATE TABLE IF NOT EXISTS s_bill_item_stock (
    stock_usage_id BIGSERIAL PRIMARY KEY,
    bill_item_id INTEGER NOT NULL REFERENCES s_bill_item(bill_item_id) ON DELETE CASCADE,
    product_id BIGINT NOT NULL REFERENCES s_product(product_id),
    quantity BIGINT NOT NULL CHECK (quantity > 0)
);

CREATE INDEX IF NOT EXISTS idx_bill_item_stock_bill_item_id
    ON s_bill_item_stock(bill_item_id);

ALTER TABLE s_bill_item
    ADD COLUMN IF NOT EXISTS stock_usage_captured BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE s_bill_item
    ADD COLUMN IF NOT EXISTS package_id BIGINT REFERENCES s_package(package_id),
    ADD COLUMN IF NOT EXISTS item_name_snapshot VARCHAR(255),
    ADD COLUMN IF NOT EXISTS unit_price_snapshot DOUBLE PRECISION;

UPDATE s_bill_item i
SET item_name_snapshot = CASE
        WHEN i.product_id IS NOT NULL THEN (SELECT p.product_name FROM s_product p WHERE p.product_id = i.product_id)
        WHEN i.service_id IS NOT NULL THEN (SELECT s.service_name FROM s_service s WHERE s.service_id = i.service_id)
        WHEN i.package_id IS NOT NULL THEN (SELECT p.package_name FROM s_package p WHERE p.package_id = i.package_id)
    END,
    unit_price_snapshot = CASE
        WHEN i.product_id IS NOT NULL THEN (SELECT p.product_price FROM s_product p WHERE p.product_id = i.product_id)
        WHEN i.service_id IS NOT NULL THEN (SELECT s.service_price FROM s_service s WHERE s.service_id = i.service_id)
        WHEN i.package_id IS NOT NULL THEN (SELECT p.package_price FROM s_package p WHERE p.package_id = i.package_id)
    END
WHERE i.item_name_snapshot IS NULL OR i.unit_price_snapshot IS NULL;

ALTER TABLE s_bill_item
    ALTER COLUMN item_name_snapshot SET NOT NULL,
    ALTER COLUMN unit_price_snapshot SET NOT NULL;

ALTER TABLE s_bill_item
    DROP CONSTRAINT IF EXISTS bill_item_one_reference;

ALTER TABLE s_bill_item
    ADD CONSTRAINT bill_item_one_reference CHECK (
        (CASE WHEN product_id IS NOT NULL THEN 1 ELSE 0 END) +
        (CASE WHEN service_id IS NOT NULL THEN 1 ELSE 0 END) +
        (CASE WHEN package_id IS NOT NULL THEN 1 ELSE 0 END) = 1
    );
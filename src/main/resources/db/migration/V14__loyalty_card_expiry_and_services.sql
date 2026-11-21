ALTER TABLE s_loyalty_card
    ADD COLUMN expiry_date DATE;

ALTER TABLE s_loyalty_card
    DROP CONSTRAINT uq_loyalty_card_client_service;

DROP INDEX IF EXISTS idx_loyalty_card_service;

CREATE TABLE s_loyalty_card_service (
    loyalty_card_id BIGINT NOT NULL REFERENCES s_loyalty_card(loyalty_card_id) ON DELETE CASCADE,
    service_id BIGINT NOT NULL REFERENCES s_service(service_id),
    PRIMARY KEY (loyalty_card_id, service_id)
);

INSERT INTO s_loyalty_card_service (loyalty_card_id, service_id)
SELECT loyalty_card_id, service_id
FROM s_loyalty_card
ON CONFLICT DO NOTHING;

ALTER TABLE s_loyalty_card
    DROP COLUMN service_id;

CREATE INDEX idx_loyalty_card_service_service
    ON s_loyalty_card_service (service_id);

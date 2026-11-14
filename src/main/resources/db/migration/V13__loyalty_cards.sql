CREATE TABLE IF NOT EXISTS s_loyalty_card (
    loyalty_card_id BIGSERIAL PRIMARY KEY,
    client_id BIGINT NOT NULL REFERENCES s_client(client_id) ON DELETE CASCADE,
    service_id BIGINT NOT NULL REFERENCES s_service(service_id),
    paid_visits INTEGER NOT NULL DEFAULT 0 CHECK (paid_visits BETWEEN 0 AND 9),
    free_visits INTEGER NOT NULL DEFAULT 0 CHECK (free_visits >= 0),
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT uq_loyalty_card_client_service UNIQUE (client_id, service_id)
);

CREATE INDEX IF NOT EXISTS idx_loyalty_card_service
    ON s_loyalty_card (service_id);

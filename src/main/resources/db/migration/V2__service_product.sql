CREATE TABLE s_service_product (
                                   id BIGSERIAL PRIMARY KEY,
                                   service_id BIGINT NOT NULL REFERENCES s_service(service_id) ON DELETE CASCADE,
                                   product_id BIGINT NOT NULL REFERENCES s_product(product_id) ON DELETE CASCADE,
                                   required_qty INTEGER NOT NULL,
                                   UNIQUE(service_id, product_id)
);
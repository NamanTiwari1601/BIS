CREATE TABLE IF NOT EXISTS s_package (
    package_id       BIGSERIAL PRIMARY KEY,
    package_name     VARCHAR(255) NOT NULL,
    description      VARCHAR(1000),
    category         VARCHAR(100),
    package_price    DOUBLE PRECISION NOT NULL,
    duration_minutes INTEGER NOT NULL,
    status           VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at       TIMESTAMP NOT NULL DEFAULT now(),
    updated_at       TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS s_package_service (
    package_service_id BIGSERIAL PRIMARY KEY,
    package_id         BIGINT NOT NULL REFERENCES s_package(package_id) ON DELETE CASCADE,
    service_id         BIGINT NOT NULL REFERENCES s_service(service_id),
    quantity           INTEGER NOT NULL DEFAULT 1,
    UNIQUE(package_id, service_id)
);

CREATE TABLE IF NOT EXISTS s_appointment (
    appointment_id   BIGSERIAL PRIMARY KEY,
    client_id        BIGINT NOT NULL REFERENCES s_client(client_id),
    staff_id         BIGINT NOT NULL REFERENCES s_usr(user_id),
    appointment_date DATE NOT NULL,
    start_time       TIME NOT NULL,
    end_time         TIME NOT NULL,
    status           VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    notes            VARCHAR(1000),
    total_amount     DOUBLE PRECISION NOT NULL DEFAULT 0,
    reminder_sent    BOOLEAN NOT NULL DEFAULT FALSE,
    created_at       TIMESTAMP NOT NULL DEFAULT now(),
    updated_at       TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT appointment_time_order CHECK (end_time > start_time)
);

CREATE INDEX IF NOT EXISTS idx_appointment_date ON s_appointment(appointment_date);
CREATE INDEX IF NOT EXISTS idx_appointment_staff_date ON s_appointment(staff_id, appointment_date);

CREATE TABLE IF NOT EXISTS s_appointment_item (
    appointment_item_id BIGSERIAL PRIMARY KEY,
    appointment_id      BIGINT NOT NULL REFERENCES s_appointment(appointment_id) ON DELETE CASCADE,
    service_id          BIGINT REFERENCES s_service(service_id),
    package_id          BIGINT REFERENCES s_package(package_id),
    item_name           VARCHAR(255) NOT NULL,
    price               DOUBLE PRECISION NOT NULL,
    duration_minutes    INTEGER NOT NULL,
    CONSTRAINT appointment_item_one_type CHECK (
        (service_id IS NOT NULL AND package_id IS NULL) OR
        (service_id IS NULL AND package_id IS NOT NULL)
    )
);
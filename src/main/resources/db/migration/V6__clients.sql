CREATE TABLE s_client (
                          client_id BIGSERIAL PRIMARY KEY,
                          client_name VARCHAR(255) NOT NULL,
                          client_phone VARCHAR(20) UNIQUE,
                          client_email VARCHAR(255),
                          status VARCHAR(10) NOT NULL DEFAULT 'Active',
                          notes VARCHAR(500),
                          created_at TIMESTAMP(6) NOT NULL DEFAULT now(),
                          updated_at TIMESTAMP(6) NOT NULL DEFAULT now()
);

INSERT INTO s_client (client_name, client_phone, client_email)
SELECT DISTINCT ON (client_ph_no) client_name, client_ph_no, NULLIF(email_id, '')
FROM s_bill
WHERE client_ph_no IS NOT NULL AND client_ph_no <> ''
ORDER BY client_ph_no, bill_date DESC;

ALTER TABLE s_bill ADD COLUMN client_id BIGINT REFERENCES s_client(client_id);

UPDATE s_bill b
SET client_id = c.client_id
FROM s_client c
WHERE b.client_ph_no = c.client_phone;
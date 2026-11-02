ALTER TABLE s_usr
    DROP COLUMN IF EXISTS blood_group,
    ADD COLUMN IF NOT EXISTS aadhaar_number VARCHAR(12),
    ADD COLUMN IF NOT EXISTS photo TEXT,
    ADD COLUMN IF NOT EXISTS joining_date DATE,
    ADD COLUMN IF NOT EXISTS work_timing VARCHAR(10) NOT NULL DEFAULT 'FULL_DAY';

CREATE UNIQUE INDEX IF NOT EXISTS ux_usr_aadhaar
    ON s_usr (aadhaar_number) WHERE aadhaar_number IS NOT NULL;
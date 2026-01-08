ALTER TABLE prescription
    ADD COLUMN IF NOT EXISTS status TEXT NOT NULL DEFAULT 'ACTIVE',
    ADD COLUMN IF NOT EXISTS notes TEXT,
    ADD COLUMN IF NOT EXISTS consultation_date DATE;

ALTER TABLE medication
    DROP COLUMN IF EXISTS condition,
    DROP COLUMN IF EXISTS medication_status,
    DROP COLUMN IF EXISTS consultation_date;

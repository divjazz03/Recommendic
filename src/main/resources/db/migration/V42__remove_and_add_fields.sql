
ALTER TABLE clinical_notes
    DROP COLUMN consultant_id,
    DROP COLUMN patient_id;
ALTER TABLE clinical_notes
    ADD COLUMN consultant_id TEXT,
    ADD COLUMN patient_id TEXT;

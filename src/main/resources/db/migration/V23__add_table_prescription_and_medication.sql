DROP INDEX IF EXISTS idx_prescribed_to CASCADE ;
DROP TABLE IF EXISTS medication, prescription CASCADE;

ALTER TABLE patient
    DROP COLUMN user_id;
ALTER TABLE patient
    ADD COLUMN user_id TEXT GENERATED ALWAYS AS ( 'PT-' || user_uuid ) STORED UNIQUE ;

ALTER TABLE consultation
    DROP COLUMN consultation_id;
ALTER TABLE consultation
    ADD COLUMN consultation_id TEXT GENERATED ALWAYS AS ( 'CS-' || consultation_uuid ) STORED UNIQUE ;

CREATE TABLE IF NOT EXISTS prescription
(
    id         BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    prescription_uuid UUID DEFAULT uuidv7() unique,
    prescription_id TEXT GENERATED ALWAYS AS ( 'PRX-' || prescription_uuid ) STORED UNIQUE ,
    self_reported BOOLEAN,
    prescriber_id TEXT,
    diagnosis TEXT NOT NULL ,
    prescribed_to BIGINT REFERENCES patient(id),
    consultation_id BIGINT REFERENCES consultation(id),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by TEXT,
    updated_by TEXT
);

CREATE INDEX IF NOT EXISTS idx_prescribed_to on prescription(prescribed_to);
CREATE INDEX IF NOT EXISTS idx_prescription_id on prescription(prescription_id);

CREATE TABLE IF NOT EXISTS medication
(
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    medication_uuid UUID DEFAULT uuidv7() UNIQUE,
    medication_id TEXT GENERATED ALWAYS AS ( 'MDC-' || medication_uuid ) STORED ,
    name TEXT NOT NULL ,
    dosage TEXT NOT NULL ,
    frequency TEXT NOT NULL ,
    start_date DATE NOT NULL ,
    end_date DATE NOT NULL ,
    condition TEXT,
    instructions TEXT,
    medication_status TEXT NOT NULL ,
    consultation_date DATE,
    prescription_id BIGINT REFERENCES prescription (id),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by TEXT,
    updated_by TEXT
);
CREATE INDEX IF NOT EXISTS idx_medication_id on medication(medication_id);
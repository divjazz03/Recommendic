CREATE TABLE IF NOT EXISTS medications
(
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    medication_id TEXT NOT NULL UNIQUE ,
    name TEXT NOT NULL ,
    dosage TEXT NOT NULL ,
    frequency TEXT NOT NULL ,
    start_date DATE NOT NULL ,
    end_date DATE NOT NULL ,
    instructions TEXT,
    prescription_id BIGINT REFERENCES prescriptions (id),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by TEXT,
    updated_by TEXT
);
CREATE INDEX IF NOT EXISTS idx_medication_id on medications(medication_id);
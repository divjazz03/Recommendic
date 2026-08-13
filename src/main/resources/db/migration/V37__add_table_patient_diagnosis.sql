
CREATE TABLE patient_diagnoses (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    consultation_id BIGINT REFERENCES consultations(id),
    code TEXT,
    coding_system TEXT,
    name TEXT,
    status TEXT,
    type TEXT,
    clinical_note TEXT,
    diagnosis_id TEXT UNIQUE NOT NULL ,
    created_by TEXT NOT NULL ,
    updated_by TEXT NOT NULL ,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
)
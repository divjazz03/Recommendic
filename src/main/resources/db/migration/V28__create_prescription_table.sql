CREATE TABLE IF NOT EXISTS prescriptions
(
    id         BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    prescription_id TEXT NOT NULL ,
    self_reported BOOLEAN,
    prescriber BIGINT REFERENCES consultants,
    diagnosis TEXT NOT NULL ,
    prescribed_to BIGINT REFERENCES patients(id),
    consultation_id BIGINT REFERENCES consultations(id),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by TEXT,
    updated_by TEXT,
    status TEXT NOT NULL DEFAULT 'ACTIVE',
    notes TEXT,
    consultation_date DATE
);

CREATE INDEX IF NOT EXISTS idx_prescribed_to on prescriptions(prescribed_to);
CREATE INDEX IF NOT EXISTS idx_prescriber on prescriptions(prescriber);
CREATE INDEX IF NOT EXISTS idx_prescription_id on prescriptions(prescription_id);

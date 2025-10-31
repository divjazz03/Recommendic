DROP TABLE IF EXISTS consultation_patient_data CASCADE;

CREATE TABLE IF NOT EXISTS consultation_patient_data (
    id BIGINT REFERENCES patient (id) ON DELETE CASCADE,
    allergies TEXT[],
    conditions TEXT[],
    last_visit TIMESTAMP WITHOUT TIME ZONE,
    insurance TEXT,
    last_recorded_vitals jsonb,
    connected_devices jsonb,
    patient_reported jsonb
);
CREATE TABLE consultation_patient_data
(
    id                   BIGINT REFERENCES patients (id) ON DELETE CASCADE,
    allergies            TEXT[],
    conditions           TEXT[],
    last_visit           TIMESTAMP WITHOUT TIME ZONE,
    insurance            TEXT,
    last_recorded_vitals jsonb,
    connected_devices    jsonb,
    patient_reported     jsonb
);
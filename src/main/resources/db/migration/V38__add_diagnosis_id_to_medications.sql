
ALTER TABLE medications
    ADD COLUMN diagnosis_id BIGINT REFERENCES patient_diagnoses(id) DEFAULT NULL;
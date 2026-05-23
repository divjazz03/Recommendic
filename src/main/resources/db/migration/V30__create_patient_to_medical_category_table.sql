
CREATE TABLE patients_medical_categories (
    patient_id BIGINT REFERENCES patients (id),
    medical_category_id BIGINT REFERENCES medical_categories (id)
)
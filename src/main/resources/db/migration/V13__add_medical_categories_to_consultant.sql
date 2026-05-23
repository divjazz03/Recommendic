ALTER TABLE IF EXISTS consultants
    ADD COLUMN specialization BIGINT REFERENCES medical_categories(id);
ALTER TABLE messages
    ADD COLUMN IF NOT EXISTS consultation_id BIGINT REFERENCES consultations (id);

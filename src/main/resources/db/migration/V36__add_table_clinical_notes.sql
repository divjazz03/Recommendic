CREATE TABLE clinical_notes (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    consultation_id BIGINT REFERENCES consultations (id) NOT NULL ,
    patient_id BIGINT REFERENCES patients (id) NOT NULL ,
    consultant_id BIGINT REFERENCES consultants (id) NOT NULL ,
    version INTEGER DEFAULT 1,
    amendment_reason TEXT DEFAULT NULL,
    subjective JSONB,
    objective JSONB,
    assessment JSONB,
    plan JSONB,
    noteId TEXT UNIQUE NOT NULL ,
    last_saved_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_edited_by TEXT,
    status TEXT DEFAULT 'DRAFT'
)
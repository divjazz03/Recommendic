CREATE TABLE IF NOT EXISTS consultation_sessions
(
    id            BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    session_id    TEXT UNIQUE NOT NULL ,
    patient_id    BIGINT REFERENCES patients (id)    NOT NULL,
    consultant_id BIGINT REFERENCES consultants (id) NOT NULL,
    updated_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by    TEXT                              NOT NULL,
    updated_by    TEXT                              NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_consultation_session_id ON consultation_sessions(session_id);

ALTER TABLE consultations
    ADD COLUMN IF NOT EXISTS session_id BIGINT REFERENCES consultation_sessions(id);
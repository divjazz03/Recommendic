

CREATE TABLE IF NOT EXISTS consultation_session (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    session_uuid UUID DEFAULT uuidv7(),
    session_id TEXT GENERATED ALWAYS AS ('CSN-' || session_uuid) STORED,
    patient_id BIGINT REFERENCES patient (id) NOT NULL ,
    consultant_id BIGINT REFERENCES consultant (id) NOT NULL ,
    updated_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by    TEXT                                          NOT NULL,
    updated_by    TEXT                                          NOT NULL
);

CREATE INDEX idx_consultant_session_id ON consultation_session(session_id);

ALTER TABLE consultation
    ADD COLUMN session_id BIGINT REFERENCES consultation_session(id);
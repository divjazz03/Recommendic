DROP TABLE IF EXISTS chat_session ;
DROP INDEX IF EXISTS idx_chat_session_session_id;


CREATE TABLE IF NOT EXISTS chat_session(
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    chat_session_id TEXT GENERATED ALWAYS AS ('CHS-' || patient_id || consultant_id ) STORED UNIQUE ,
    patient_id TEXT,
    consultant_id TEXT,
    updated_at      TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_at      TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by      TEXT,
    updated_by      TEXT
);

CREATE INDEX idx_chat_session_session_id on chat_session(chat_session_id);

ALTER TABLE message
    ADD COLUMN session TEXT REFERENCES chat_session(chat_session_id);

ALTER TABLE appointment
 ADD COLUMN history TEXT NOT NULL DEFAULT 'NEW';

ALTER TABLE consultation_session
 ADD COLUMN patient_status TEXT,
    ADD COLUMN condition TEXT;
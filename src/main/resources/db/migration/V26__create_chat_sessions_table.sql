
CREATE TABLE IF NOT EXISTS chat_sessions(
                                           id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
                                           chat_session_id TEXT UNIQUE NOT NULL ,
                                           patient_id TEXT REFERENCES patients (user_id),
                                           consultant_id TEXT REFERENCES consultants (user_id),
                                           updated_at      TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                                           created_at      TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                                           created_by      TEXT,
                                           updated_by      TEXT
);

CREATE INDEX idx_chat_session_session_id on chat_sessions(chat_session_id);

ALTER TABLE messages
    ADD COLUMN IF NOT EXISTS session TEXT REFERENCES chat_sessions(chat_session_id);

ALTER TABLE appointments
    ADD COLUMN IF NOT EXISTS history TEXT NOT NULL DEFAULT 'NEW';

ALTER TABLE consultation_sessions
    ADD COLUMN IF NOT EXISTS patient_status TEXT,
    ADD COLUMN IF NOT EXISTS condition TEXT;
ALTER TABLE IF EXISTS messages
    ADD COLUMN IF NOT EXISTS session TEXT REFERENCES chat_sessions (chat_session_id);
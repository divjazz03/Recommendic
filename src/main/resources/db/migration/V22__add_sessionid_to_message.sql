ALTER TABLE message
    DROP COLUMN IF EXISTS session;
ALTER TABLE message
    ADD COLUMN IF NOT EXISTS session TEXT REFERENCES chat_session (chat_session_id);
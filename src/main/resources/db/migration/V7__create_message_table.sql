BEGIN;
CREATE TABLE IF NOT EXISTS messages
(
    id            BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    sender_id     TEXT NOT NULL ,
    receiver_id   TEXT NOT NULL ,
    content       TEXT NOT NULL ,
    timestamp     TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP,
    search_vector tsvector GENERATED ALWAYS AS (
        setweight(to_tsvector('english', coalesce(content, '')), 'C')
        ) STORED,
    delivered     BOOLEAN      DEFAULT FALSE,
    updated_at    TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP,
    created_at    TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP,
    created_by    TEXT NOT NULL ,
    updated_by    TEXT

);
CREATE INDEX IF NOT EXISTS message_search_idx ON messages USING GIN (search_vector);
END;
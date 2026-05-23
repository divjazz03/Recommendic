CREATE TABLE IF NOT EXISTS consultations
(
    id              BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    consultation_id TEXT NOT NULL UNIQUE,
    channel         TEXT NOT NULL,
    status          TEXT NOT NULL,
    appointment_id  BIGINT REFERENCES appointments (id) ON DELETE CASCADE,
    search_vector   TSVECTOR,
    summary         TEXT,
    started_at      TIMESTAMP,
    ended_at        TIMESTAMP,
    updated_at      TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_at      TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by      TEXT,
    updated_by      TEXT
);
CREATE INDEX IF NOT EXISTS consultation_search_idx ON consultations USING GIN (search_vector);
CREATE INDEX IF NOT EXISTS idx_consultation_id ON consultations (consultation_id);

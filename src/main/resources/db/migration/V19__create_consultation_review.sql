CREATE TABLE consultation_reviews
(
    id              BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    consultation_id BIGINT REFERENCES consultations (id) NOT NULL,
    rating          INTEGER                  DEFAULT 0,
    comment         TEXT NOT NULL ,
    name            TEXT NOT NULL ,
    date            TIMESTAMP WITH TIME ZONE,
    updated_at      TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_at      TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by      TEXT,
    updated_by      TEXT
);
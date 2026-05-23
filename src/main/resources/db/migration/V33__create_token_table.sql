CREATE TABLE tokens (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    token TEXT UNIQUE NOT NULL ,
    user_id TEXT NOT NULL,
    user_type TEXT,
    expires_at TIMESTAMP WITHOUT TIME ZONE,
    created_at TIMESTAMP DEFAULT now(),
    updated_at TIMESTAMP DEFAULT now(),
    created_by TEXT DEFAULT now(),
    updated_by TEXT DEFAULT now()
);
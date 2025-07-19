DROP TABLE IF EXISTS admin_profiles;

CREATE TABLE admin_profiles
(
    id              BIGINT PRIMARY KEY REFERENCES admin (id) ON DELETE CASCADE,
    profile_picture jsonb,
    address         jsonb,
    phone_number    TEXT,
    username        jsonb,
    updated_at      TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP,
    created_at      TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP,
    search_vector   tsvector GENERATED ALWAYS AS (
                            setweight(to_tsvector('english', coalesce(username ->> 'first_name', '')), 'A') ||
                            setweight(to_tsvector('english', coalesce(username ->> 'last_name', '')), 'B') ) STORED,
    created_by      TEXT NOT NULL,
    updated_by      TEXT NOT NULL
);
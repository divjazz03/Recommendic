CREATE TABLE IF NOT EXISTS users_confirmations
(
    id         BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    user_id    TEXT         NOT NULL,
    expiry     TIMESTAMP(6) NOT NULL,
    key        TEXT         NOT NULL,
    updated_at TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP,
    created_by TEXT         NOT NULL,
    updated_by TEXT         NOT NULL
);
CREATE TABLE IF NOT EXISTS comments
(
    id                BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    comment_id        TEXT NOT NULL UNIQUE ,
    user_id           TEXT NOT NULL,
    article_id        BIGINT REFERENCES articles (id),
    parent_comment_id BIGINT REFERENCES comments (id),
    updated_at        TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP,
    created_at        TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP,
    created_by        TEXT,
    updated_by        TEXT
);
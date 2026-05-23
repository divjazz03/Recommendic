BEGIN;
CREATE TABLE IF NOT EXISTS articles
(
    id             BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    article_id     TEXT UNIQUE NOT NULL ,
    title          TEXT                              NOT NULL,
    subtitle       TEXT                              NOT NULL,
    content        TEXT                              NOT NULL,
    like_ids       BIGINT[],
    tags           TEXT[],
    writer_id      BIGINT REFERENCES consultants (id) ON DELETE CASCADE NOT NULL,
    no_of_reads    BIGINT       DEFAULT 0,
    article_status VARCHAR(10)  DEFAULT 'DRAFT',
    search_vector  TSVECTOR GENERATED ALWAYS AS (
        setweight(to_tsvector('english', coalesce(title, '')), 'A') ||
        setweight(to_tsvector('english', coalesce(content, '')), 'B') ||
        setweight(to_tsvector('english', coalesce(subtitle, '')), 'C')
        ) STORED,
    updated_at     TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP,
    created_at     TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP,
    published_at   TIMESTAMP(6) DEFAULT NULL,
    created_by     TEXT                              NOT NULL,
    updated_by     TEXT                              NOT NULL

);

CREATE INDEX IF NOT EXISTS article_search_idx ON articles USING GIN (search_vector);
CREATE INDEX IF NOT EXISTS article_status_idx on articles (article_status);
END;
DROP TABLE IF EXISTS app_notification CASCADE ;
DROP TYPE IF EXISTS notification_category CASCADE;


CREATE TYPE notification_category
AS ENUM ('ARTICLE', 'APPOINTMENT', 'CONSULTATION', 'USER', 'CHAT');

CREATE TABLE IF NOT EXISTS app_notification (
    id            BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY NOT NULL,
    notification_uuid           UUID DEFAULT uuidv7() UNIQUE,
    notification_id             TEXT GENERATED ALWAYS AS ( 'NTF-' || notification_uuid ) STORED ,
    header TEXT NOT NULL,
    summary TEXT,
    category notification_category NOT NULL ,
    seen    boolean,
    user_id       TEXT NOT NULL,
    subject_id    TEXT NOT NULL,
    updated_at    TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_at    TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by    TEXT,
    updated_by    TEXT
)
CREATE TABLE notifications
(
    id              BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    notification_id TEXT NOT NULL UNIQUE,
    header          TEXT NOT NULL,
    summary         TEXT,
    category        TEXT NOT NULL               DEFAULT 'GENERAL',
    seen            boolean,
    user_id         TEXT NOT NULL,
    subject_id      TEXT NOT NULL,
    updated_at      TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_at      TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by      TEXT,
    updated_by      TEXT
);

CREATE INDEX idx_notification_id ON notifications (notification_id);
CREATE INDEX idx_notification_header ON notifications (header);
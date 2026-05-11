
ALTER TABLE app_notification
    DROP COLUMN category;

ALTER TABLE app_notification
    ADD COLUMN category TEXT NOT NULL DEFAULT 'GENERAL';

DROP TYPE IF EXISTS notification_category;
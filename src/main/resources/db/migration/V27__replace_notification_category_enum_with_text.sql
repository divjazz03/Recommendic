
ALTER TABLE app_notification
    DROP COLUMN category;

ALTER TABLE recommendic.public.app_notification
    ADD COLUMN category TEXT NOT NULL DEFAULT 'GENERAL';

DROP TYPE notification_category;
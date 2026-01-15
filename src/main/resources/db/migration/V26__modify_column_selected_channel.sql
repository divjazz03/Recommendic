ALTER TABLE appointment
    DROP COLUMN IF EXISTS selected_channel;
ALTER TABLE appointment
    ADD COLUMN IF NOT EXISTS selected_channel TEXT NOT NULL DEFAULT 'ONLINE';
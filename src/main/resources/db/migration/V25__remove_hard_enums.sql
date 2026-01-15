ALTER TABLE schedule_slot
    DROP COLUMN consultation_channel;
ALTER TABLE schedule_slot
    ADD COLUMN consultation_channel TEXT[] NOT NULL DEFAULT '{ONLINE}';
ALTER TABLE appointment
    DROP COLUMN status;
ALTER TABLE appointment
    ADD COLUMN status TEXT NOT NULL DEFAULT 'PENDING';
ALTER TABLE consultation
    DROP COLUMN status,
    DROP COLUMN channel;
ALTER TABLE consultation
    ADD COLUMN status TEXT NOT NULL DEFAULT 'ONGOING',
    ADD COLUMN channel TEXT NOT NULL DEFAULT 'ONLINE';

DROP TYPE IF EXISTS session_channel,consultation_status, appointment_status CASCADE;

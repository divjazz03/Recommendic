DROP TYPE IF EXISTS appointment_priority;
CREATE TYPE appointment_priority as ENUM('LOW','MEDIUM','HIGH');
ALTER TABLE appointment
    ADD COLUMN cancellation_reason TEXT,
    ADD COLUMN priority appointment_priority default 'LOW',
    ADD COLUMN symptoms TEXT;
DROP TABLE IF EXISTS consultation, schedule_slot, appointment, consultation_review CASCADE;
DROP INDEX IF EXISTS consultation_search_idx;
DROP TYPE IF EXISTS session_channel,consultation_status, appointment_status;

CREATE TYPE session_channel AS ENUM ('VOICE','CHAT', 'VIDEO','IN_PERSON');
CREATE TYPE consultation_status AS ENUM ('ONGOING','COMPLETED', 'MISSED');
CREATE TYPE appointment_status AS ENUM ('REQUESTED','CONFIRMED', 'CANCELLED');

CREATE TABLE IF NOT EXISTS schedule_slot
(
    id                   BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY NOT NULL,
    schedule_uuid UUID                        DEFAULT gen_random_uuid() UNIQUE,
    appointment_id   TEXT GENERATED ALWAYS AS ( 'SCH-' || schedule_uuid ) STORED,
    consultant_id        BIGINT REFERENCES consultant (id)               NOT NULL,
    start_time           TIME                                            NOT NULL,
    end_time             TIME                                            NOT NULL,
    utf_offset           VARCHAR                                         NOT NULL,
    consultation_channel session_channel[]                               NOT NULL,
    is_recurring         BOOLEAN                     DEFAULT FALSE,
    recurrence_rule      TEXT,
    is_active            BOOLEAN                     DEFAULT FALSE,
    name                 TEXT                                            NOT NULL,
    updated_at           TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_at           TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by           TEXT,
    updated_by           TEXT
);

CREATE TABLE IF NOT EXISTS appointment
(
    id               BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY        NOT NULL,
    appointment_uuid UUID                        DEFAULT gen_random_uuid() UNIQUE,
    appointment_id   TEXT GENERATED ALWAYS AS ( 'APT-' || appointment_uuid ) STORED,
    patient_id       BIGINT REFERENCES patient (id) ON DELETE RESTRICT      NOT NULL,
    consultant_id    BIGINT REFERENCES consultant (id) ON DELETE RESTRICT   NOT NULL,
    schedule_slot_id BIGINT REFERENCES schedule_slot (id) ON DELETE CASCADE NOT NULL,
    note             TEXT,
    status           appointment_status          DEFAULT 'REQUESTED',
    date             date                                                   NOT NULL,
    selected_channel session_channel,
    updated_at       TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_at       TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by       TEXT,
    updated_by       TEXT
);


CREATE TABLE IF NOT EXISTS consultation
(
    id              BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    consultation_uuid           UUID DEFAULT gen_random_uuid() UNIQUE,
    consultation_id             TEXT GENERATED ALWAYS AS ( 'CS-' || consultation_uuid ) STORED ,
    channel         session_channel                                               NOT NULL,
    status          consultation_status                                           NOT NULL,
    appointment_id  BIGINT REFERENCES appointment (id),
    search_vector   TSVECTOR,
    summary         TEXT,
    started_at      TIMESTAMP,
    ended_at        TIMESTAMP,
    updated_at      TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_at      TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by      TEXT,
    updated_by      TEXT
);

CREATE TABLE consultation_review
(
    id              BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    consultation_id BIGINT REFERENCES consultation (id) NOT NULL,
    rating          INTEGER                  DEFAULT 0,
    comment         TEXT,
    date            TIMESTAMP WITHOUT TIME ZONE,
    updated_at      TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_at      TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by      TEXT,
    updated_by      TEXT
);

ALTER TABLE message
    ADD COLUMN IF NOT EXISTS consultation_id BIGINT REFERENCES consultation (id);

CREATE OR REPLACE FUNCTION update_consultation_search_vector()
    RETURNS TRIGGER AS
$$
BEGIN
    NEW.search_vector := setweight(to_tsvector('english', coalesce(NEW.channel::text, 'CHAT')), 'B') ||
                         setweight(to_tsvector('english', coalesce(NEW.status::text, '')), 'A');
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_update_search_vector
    BEFORE INSERT OR UPDATE
    ON consultation
    FOR EACH ROW
EXECUTE FUNCTION update_consultation_search_vector();

ALTER TABLE message
    ADD COLUMN IF NOT EXISTS consultation_id BIGINT REFERENCES consultation (id);
ALTER TABLE consultation_review
    ADD COLUMN IF NOT EXISTS consultation_id BIGINT REFERENCES consultation (id);

CREATE INDEX IF NOT EXISTS consultation_search_idx ON consultation USING GIN (search_vector);

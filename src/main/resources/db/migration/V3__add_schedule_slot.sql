DROP TABLE IF EXISTS consultation, schedule_slot, appointment CASCADE;
DROP INDEX IF EXISTS consultation_search_idx;
DROP TYPE IF EXISTS session_channel,consultation_status, appointment_status;

CREATE TYPE session_channel AS ENUM ('VOICE','CHAT', 'VIDEO','IN_PERSON');
CREATE TYPE consultation_status AS ENUM ('ONGOING','COMPLETED', 'MISSED');
CREATE TYPE  appointment_status AS ENUM ('REQUESTED','CONFIRMED', 'CANCELED');

CREATE TABLE IF NOT EXISTS schedule_slot
(
    id                   BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY NOT NULL,
    consultant_id        BIGINT REFERENCES consultant (id)               NOT NULL,
    start_time           TIMESTAMP WITH TIME ZONE                        NOT NULL,
    end_time             TIMESTAMP WITH TIME ZONE                        NOT NULL,
    consultation_channel session_channel                                 NOT NULL,
    is_recurring         BOOLEAN                        DEFAULT FALSE,
    recurrence_rule      TEXT,
    is_booked            BOOLEAN                        DEFAULT FALSE,
    updated_at           TIMESTAMP(6) WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_at           TIMESTAMP(6) WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by           CHARACTER VARYING(54),
    updated_by           CHARACTER VARYING(54)
);

CREATE TABLE IF NOT EXISTS appointment
(
    id            BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY NOT NULL,
    patient_id    BIGINT REFERENCES patient_schema.patient (id),
    consultant_id BIGINT REFERENCES consultant (id),
    schedule_slot_id BIGINT REFERENCES schedule_slot (id),
    note          TEXT,
    status        appointment_status        DEFAULT 'REQUESTED',
    updated_at    TIMESTAMP(6) WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_at    TIMESTAMP(6) WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by    CHARACTER VARYING(54),
    updated_by    CHARACTER VARYING(54)
);


CREATE TABLE IF NOT EXISTS consultation
(
    id            BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    channel       session_channel     NOT NULL,
    status        consultation_status NOT NULL,
    appointment_id BIGINT REFERENCES appointment (id),
    search_vector TSVECTOR ,
    summary         TEXT,
    started_at    TIMESTAMP,
    ended_at      TIMESTAMP,
    updated_at    TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_at    TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by    CHARACTER VARYING(54),
    updated_by    CHARACTER VARYING(54)
);

CREATE OR REPLACE FUNCTION update_consultation_search_vector()
RETURNS TRIGGER AS $$
    BEGIN
        NEW.search_vector := setweight(to_tsvector('english', coalesce(NEW.channel::text, 'CHAT')), 'B') ||
                             setweight(to_tsvector('english', coalesce(NEW.status::text, '')), 'A');
    END;
    $$ LANGUAGE plpgsql;
CREATE TRIGGER trg_update_search_vector
    BEFORE INSERT OR UPDATE ON consultation
    FOR EACH ROW EXECUTE FUNCTION update_consultation_search_vector();

ALTER TABLE message
    ADD COLUMN IF NOT EXISTS consultation_id BIGINT REFERENCES consultation (id);
ALTER TABLE consultation_review
    ADD COLUMN IF NOT EXISTS consultation_id BIGINT REFERENCES consultation (id);

CREATE INDEX IF NOT EXISTS consultation_search_idx ON consultation USING GIN (search_vector);

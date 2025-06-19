DROP TABLE IF EXISTS consultation_type,
    consultant_education, consultant_stat, consultation_review CASCADE;
DROP INDEX IF EXISTS idx_consultant_stat, idx_consultant_review CASCADE;

CREATE TABLE consultation_type
(
    id          BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    name        CHARACTER VARYING(20) NOT NULL,
    price       DECIMAL               NOT NULL,
    description TEXT                  NOT NULL,
    updated_at        TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_at        TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by        CHARACTER VARYING(54),
    updated_by        CHARACTER VARYING(54)
);

ALTER TABLE consultation
    ADD COLUMN IF NOT EXISTS type_id BIGINT REFERENCES consultation_type (id);

ALTER TABLE consultant
    ADD COLUMN IF NOT EXISTS languages  TEXT[],
    ADD COLUMN IF NOT EXISTS location   TEXT DEFAULT NULL,
    ADD COLUMN IF NOT EXISTS experience INT  DEFAULT NULL,
    ADD COLUMN IF NOT EXISTS title      TEXT DEFAULT NULL;

CREATE TABLE consultant_education
(
    id            BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    consultant_id BIGINT REFERENCES consultant (id) NOT NULL,
    degree        TEXT                              NOT NULL,
    institution   TEXT                              NOT NULL,
    year          Integer                           NOT NULL,
    updated_at        TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_at        TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by        CHARACTER VARYING(54),
    updated_by        CHARACTER VARYING(54)
);

CREATE TABLE consultant_stat
(
    id               BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    consultant_id    BIGINT REFERENCES consultant (id),
    patients_helped  TEXT[],
    successes        TEXT[],
    success_rate     INTEGER GENERATED ALWAYS AS (
                         round((cardinality(successes)::decimal / NULLIF(cardinality(patients_helped), 0)) *
                               100)) STORED,
    response_times   INTEGER[],
    average_response INTEGER,
    follow_ups       TEXT[],
    updated_at        TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_at        TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by        CHARACTER VARYING(54),
    updated_by        CHARACTER VARYING(54)
);

CREATE OR REPLACE FUNCTION update_average_response()
    RETURNS TRIGGER AS
$$
    BEGIN
        IF NEW.response_times IS NOT NULL AND cardinality(NEW.response_times) > 0 THEN
            SELECT round(avg(val))
            INTO NEW.average_response
            FROM unnest(NEW.response_times) AS val;
        ELSE
            NEW.average_response := NULL;
        END IF;
    END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_update_average_response
    BEFORE INSERT OR UPDATE ON consultant_stat
    FOR EACH ROW
    EXECUTE FUNCTION update_average_response();

ALTER TABLE consultant
ADD COLUMN IF NOT EXISTS consultant_stat_id BIGINT REFERENCES consultant_stat(id);

CREATE TABLE consultation_review
(
    id              BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    consultation_id BIGINT REFERENCES consultation (id),
    rating          INTEGER DEFAULT 0,
    comment         TEXT,
    date            TIMESTAMP(6) WITHOUT TIME ZONE,
    updated_at        TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_at        TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by        CHARACTER VARYING(54),
    updated_by        CHARACTER VARYING(54)
);

CREATE INDEX idx_consultant_stat ON consultant_stat (success_rate);
CREATE INDEX idx_consultant_review ON consultation_review (rating);

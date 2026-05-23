CREATE TABLE consultant_stats
(
    id BIGINT PRIMARY KEY REFERENCES consultants (id) ON DELETE CASCADE,
    patients_helped  TEXT[],
    successes        TEXT[],
    success_rate     INTEGER GENERATED ALWAYS AS (
        round((cardinality(successes)::decimal / NULLIF(cardinality(patients_helped), 0)) *
              100)) STORED,
    response_times   INTEGER[],
    average_response INTEGER,
    follow_ups       TEXT[],
    updated_at       TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_at       TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by       TEXT,
    updated_by       TEXT
);
CREATE TABLE schedule_slots
(
    id                   BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY NOT NULL,
    schedule_id          TEXT UNIQUE NOT NULL,
    consultant_id        BIGINT REFERENCES consultants (id) NOT NULL,
    start_time           TIME                                            NOT NULL,
    end_time             TIME                                            NOT NULL,
    utf_offset           VARCHAR                                         NOT NULL,
    consultation_channels TEXT[]                               NOT NULL,
    recurrence_rule      JSONB,
    is_active            BOOLEAN                     DEFAULT FALSE,
    name                 TEXT                                            NOT NULL,
    updated_at           TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_at           TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by           TEXT,
    updated_by           TEXT
);

CREATE INDEX idx_schedule_slot_recurrence_rule ON schedule_slots USING gin(recurrence_rule);
CREATE INDEX idx_schedule_slot_id ON schedule_slots (schedule_id);



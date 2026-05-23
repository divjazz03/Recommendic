CREATE TABLE appointments
(
    id               BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY        NOT NULL,
    appointment_id   TEXT UNIQUE NOT NULL ,
    patient_id       BIGINT REFERENCES patients (id) ON DELETE CASCADE  NOT NULL,
    consultant_id    BIGINT REFERENCES consultants (id) ON DELETE CASCADE  NOT NULL,
    schedule_slot_id BIGINT REFERENCES schedule_slots (id) ON DELETE CASCADE NOT NULL,
    note             TEXT,
    status           TEXT          DEFAULT 'PENDING',
    date             date                                                   NOT NULL,
    selected_channel TEXT NOT NULL DEFAULT 'ONLINE',
    updated_at       TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_at       TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by       TEXT,
    updated_by       TEXT,
    reason           TEXT DEFAULT 'User did not Unspecify',
    cancellation_reason TEXT,
    priority TEXT default 'LOW',
    symptoms TEXT
);

CREATE INDEX IF NOT EXISTS idx_appointment ON appointments(appointment_id);
DROP INDEX IF EXISTS idx_appointment, idx_consultation_id, idx_schedule_slot_id;

CREATE INDEX IF NOT EXISTS idx_schedule_slot_id ON schedule_slot (schedule_id);
CREATE INDEX IF NOT EXISTS idx_consultation_id ON consultation (consultation_id);
CREATE INDEX IF NOT EXISTS idx_appointment ON appointment(appointment_id);
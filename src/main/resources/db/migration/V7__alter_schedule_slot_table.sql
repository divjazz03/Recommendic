ALTER TABLE schedule_slot DROP COLUMN recurrence_rule;
ALTER TABLE schedule_slot ADD COLUMN recurrence_rule jsonb;

CREATE INDEX idx_schedule_slot_recurrence_rule ON schedule_slot USING gin(recurrence_rule);
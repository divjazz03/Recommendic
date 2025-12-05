

ALTER TABLE consultant_profiles
    ADD COLUMN IF NOT EXISTS sub_specialties TEXT[],
    ADD COLUMN IF NOT EXISTS license_number TEXT,
    ADD COLUMN IF NOT EXISTS preferred_timeslots TEXT[],
    ADD COLUMN IF NOT EXISTS available_days_of_week TEXT[],
    ADD COLUMN IF NOT EXISTS certifications TEXT,
    ADD COLUMN IF NOT EXISTS online_consultation_fee INTEGER,
    ADD COLUMN IF NOT EXISTS consultation_duration INTEGER

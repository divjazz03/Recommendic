DROP TYPE IF EXISTS blood_type;


CREATE TYPE blood_type AS ENUM ('A+','A-','B+','B-','AB+','AB-','0+','0-');
ALTER TABLE IF EXISTS patient_profiles
    ADD COLUMN IF NOT EXISTS emergency_contact_name TEXT,
    ADD COLUMN IF NOT EXISTS emergency_contact_phone_number TEXT,
    ADD COLUMN IF NOT EXISTS blood_type blood_type,
    ADD COLUMN IF NOT EXISTS medical_history jsonb,
    ADD COLUMN IF NOT EXISTS lifestyle_info jsonb
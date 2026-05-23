BEGIN;
CREATE TABLE IF NOT EXISTS patient_profiles
(
    id              BIGINT PRIMARY KEY REFERENCES patients (id) ON DELETE CASCADE ,
    profile_picture jsonb,
    address         jsonb,
    phone_number    TEXT,
    date_of_birth   DATE,
    username        jsonb,
    updated_at      TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP,
    created_at      TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP,
    search_vector   tsvector GENERATED ALWAYS AS (
        setweight(to_tsvector('english', coalesce(username ->> 'first_name', '')), 'A') ||
        setweight(to_tsvector('english', coalesce(username ->> 'last_name', '')), 'B') ) STORED,
    created_by      TEXT NOT NULL,
    updated_by      TEXT NOT NULL,
    emergency_contact_name TEXT,
    emergency_contact_phone_number TEXT,
    blood_type CHAR(3) CHECK (blood_type in ('A+','A-','B+','B-','AB+','AB-','0+','0-')),
    medical_history jsonb,
    lifestyle_info jsonb
);

CREATE TABLE IF NOT EXISTS consultant_profiles
(
    id              BIGINT PRIMARY KEY REFERENCES consultants (id) ON DELETE CASCADE ,
    profile_picture jsonb,
    address         jsonb,
    bio             TEXT         DEFAULT NULL,
    phone_number    TEXT,
    date_of_birth DATE,
    username        jsonb,
    location        TEXT,
    experience      INTEGER,
    title           TEXT,
    languages       TEXT[],
    updated_at      TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP,
    created_at      TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP,
    search_vector   tsvector GENERATED ALWAYS AS (
        setweight(to_tsvector('english', coalesce(username ->> 'first_name', '')), 'A') ||
        setweight(to_tsvector('english', coalesce(username ->> 'last_name', '')), 'B') ) STORED,
    created_by      TEXT NOT NULL,
    updated_by      TEXT NOT NULL,
    sub_specialties TEXT[],
    license_number TEXT,
    preferred_timeslots TEXT[],
    available_days_of_week TEXT[],
    certifications TEXT,
    online_consultation_fee INTEGER,
    consultation_duration INTEGER
);
CREATE TABLE admin_profiles
(
    id              BIGINT PRIMARY KEY REFERENCES admins (id) ON DELETE CASCADE,
    profile_picture jsonb,
    address         jsonb,
    phone_number    TEXT,
    username        jsonb,
    updated_at      TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP,
    created_at      TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP,
    search_vector   tsvector GENERATED ALWAYS AS (
        setweight(to_tsvector('english', coalesce(username ->> 'first_name', '')), 'A') ||
        setweight(to_tsvector('english', coalesce(username ->> 'last_name', '')), 'B') ) STORED,
    created_by      TEXT NOT NULL,
    updated_by      TEXT NOT NULL
);
END;

CREATE INDEX IF NOT EXISTS idx_consultant_profile_search_vector ON consultant_profiles USING gin(search_vector);

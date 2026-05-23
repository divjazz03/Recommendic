BEGIN;
CREATE EXTENSION IF NOT EXISTS pg_trgm;
CREATE EXTENSION IF NOT EXISTS unaccent;

CREATE TABLE IF NOT EXISTS roles
(
    id          BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    name        TEXT UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS permissions (
    id          BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    scope       TEXT NOT NULL,
    resource    TEXT NOT NULL,
    action      TEXT[] NOT NULL,
    role_id     BIGINT REFERENCES roles (id)
);


/* --------------------------------------------- USER TABLES ----------------------------------*/
/*                                              USER TABLE                                                             */
CREATE TABLE IF NOT EXISTS admins
(
    id                  BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    user_id             TEXT UNIQUE NOT NULL ,
    email               TEXT UNIQUE NOT NULL,
    user_type           TEXT        NOT NULL,
    user_stage          TEXT        NOT NULL,
    enabled             BOOLEAN     NOT NULL DEFAULT FALSE,
    account_non_expired BOOLEAN     NOT NULL DEFAULT TRUE,
    account_non_locked  BOOLEAN     NOT NULL DEFAULT TRUE,
    gender              TEXT        NOT NULL,
    role                BIGINT REFERENCES roles (id),
    last_login          TIMESTAMP            DEFAULT NULL,
    updated_at          TIMESTAMP            DEFAULT CURRENT_TIMESTAMP,
    created_at          TIMESTAMP            DEFAULT CURRENT_TIMESTAMP,
    created_by          TEXT,
    updated_by          TEXT,
    /*Credential embed*/
    user_credential     jsonb

);

CREATE TABLE IF NOT EXISTS patients
(
    id                  BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    user_id             TEXT UNIQUE NOT NULL,
    email               TEXT UNIQUE NOT NULL,
    user_type           TEXT        NOT NULL,
    user_stage          TEXT        NOT NULL,
    enabled             BOOLEAN     NOT NULL DEFAULT FALSE,
    account_non_expired BOOLEAN     NOT NULL DEFAULT TRUE,
    account_non_locked  BOOLEAN     NOT NULL DEFAULT TRUE,
    gender              TEXT        NOT NULL,
    role                BIGINT REFERENCES roles(id),
    last_login          TIMESTAMP            DEFAULT NULL,
    updated_at          TIMESTAMP            DEFAULT CURRENT_TIMESTAMP,
    created_at          TIMESTAMP            DEFAULT CURRENT_TIMESTAMP,
    created_by          TEXT,
    updated_by          TEXT,
    recommendation_id   BIGINT,
    user_credential     jsonb,
    notification_preference jsonb,
    security_settings jsonb

);

CREATE TABLE IF NOT EXISTS consultants
(
    id                  BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    user_id             TEXT UNIQUE NOT NULL,
    email               TEXT UNIQUE NOT NULL,
    user_type           TEXT        NOT NULL,
    user_stage          TEXT        NOT NULL,
    enabled             BOOLEAN     NOT NULL DEFAULT FALSE,
    account_non_expired BOOLEAN     NOT NULL DEFAULT TRUE,
    account_non_locked  BOOLEAN     NOT NULL DEFAULT TRUE,
    gender              TEXT        NOT NULL,
    role                BIGINT REFERENCES roles (id),
    last_login          TIMESTAMP            DEFAULT NULL,
    updated_at          TIMESTAMP            DEFAULT CURRENT_TIMESTAMP,
    created_at          TIMESTAMP            DEFAULT CURRENT_TIMESTAMP,
    created_by          TEXT,
    updated_by          TEXT,
    certified           BOOLEAN              DEFAULT FALSE,
    user_credential     jsonb,
    notification_preference jsonb,
    security_settings jsonb,
    languages  TEXT[],
    location   TEXT DEFAULT NULL,
    experience INT  DEFAULT NULL,
    title      TEXT DEFAULT NULL
);
CREATE INDEX IF NOT EXISTS idx_patient_email ON patients (email);
CREATE INDEX IF NOT EXISTS idx_patient_user_id ON patients (user_id);
CREATE INDEX IF NOT EXISTS idx_consultant_email ON consultants (email);
CREATE INDEX IF NOT EXISTS idx_consultant_user_id ON consultants (user_id);
CREATE INDEX IF NOT EXISTS idx_patient_credential ON patients USING GIN (user_credential);
CREATE INDEX IF NOT EXISTS idx_consultant_credential ON consultants USING GIN (user_credential);
END;

CREATE TABLE IF NOT EXISTS patient_security_setting
(
    id         BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    patient_id BIGINT REFERENCES patient (id) ON DELETE CASCADE,
    multi_factor_auth_enabled boolean,
    session_timeout_min integer,
    login_alerts_enabled boolean,
    updated_at TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP,
    created_by TEXT,
    updated_by TEXT
);

CREATE TABLE IF NOT EXISTS consultant_security_setting
(
    id            BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    consultant_id BIGINT REFERENCES consultant (id) ON DELETE CASCADE,
    multi_factor_auth_enabled boolean,
    session_timeout_min integer,
    login_alerts_enabled boolean,
    updated_at    TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP,
    created_at    TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP,
    created_by    TEXT,
    updated_by    TEXT
);
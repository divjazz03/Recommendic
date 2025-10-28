DROP TABLE IF EXISTS patient_notification_setting, consultant_notification_setting;

CREATE TABLE IF NOT EXISTS patient_notification_setting (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    user_id BIGINT NOT NULL REFERENCES patient(id),
    email_notification_enabled BOOLEAN DEFAULT TRUE,
    sms_notification_enabled BOOLEAN DEFAULT TRUE,
    appointment_reminders_enabled BOOLEAN DEFAULT TRUE,
    lab_results_update_enabled BOOLEAN DEFAULT TRUE,
    system_updates_enabled BOOLEAN DEFAULT FALSE,
    marketing_email_enabled BOOLEAN DEFAULT FALSE,
    updated_at          TIMESTAMP            DEFAULT CURRENT_TIMESTAMP,
    created_at          TIMESTAMP            DEFAULT CURRENT_TIMESTAMP,
    created_by          TEXT,
    updated_by          TEXT
);
CREATE TABLE IF NOT EXISTS consultant_notification_setting (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    user_id BIGINT NOT NULL REFERENCES consultant(id),
    email_notification_enabled BOOLEAN DEFAULT TRUE,
    sms_notification_enabled BOOLEAN DEFAULT TRUE,
    appointment_reminders_enabled BOOLEAN DEFAULT TRUE,
    lab_results_update_enabled BOOLEAN DEFAULT TRUE,
    system_updates_enabled BOOLEAN DEFAULT FALSE,
    marketing_email_enabled BOOLEAN DEFAULT FALSE,
    updated_at          TIMESTAMP            DEFAULT CURRENT_TIMESTAMP,
    created_at          TIMESTAMP            DEFAULT CURRENT_TIMESTAMP,
    created_by          TEXT,
    updated_by          TEXT
);
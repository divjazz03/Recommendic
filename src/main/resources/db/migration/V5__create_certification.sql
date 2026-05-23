CREATE TABLE IF NOT EXISTS certifications
(
    id               BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    certification_id             TEXT UNIQUE NOT NULL ,
    consultant_id    BIGINT REFERENCES consultants (id) NOT NULL,
    assignment_id    BIGINT REFERENCES assignments (id),
    file_name        TEXT                              NOT NULL,
    file_url         TEXT                              NOT NULL,
    certificate_type TEXT                              NOT NULL,
    confirmed        BOOLEAN      DEFAULT TRUE,
    updated_at       TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP,
    created_at       TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP,
    created_by       TEXT                              NOT NULL,
    updated_by       TEXT                              NOT NULL
);
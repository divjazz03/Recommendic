CREATE TABLE consultant_educations
(
    id            BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    consultant_id BIGINT REFERENCES consultants (id) ON DELETE CASCADE NOT NULL,
    degree        TEXT                              NOT NULL,
    institution   TEXT                              NOT NULL,
    year          Integer                           NOT NULL,
    updated_at    TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_at    TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by    TEXT,
    updated_by    TEXT
);
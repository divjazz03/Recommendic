DROP TABLE IF EXISTS notification CASCADE ;
DROP TYPE IF EXISTS notification_category;


CREATE TYPE notification_category
    AS ENUM ('NEW_ARTICLE', 'NEW_APPOINTMENT', 'CONSULTATION_STARTED', 'CONSULTATION_ENDED');

CREATE TABLE IF NOT EXISTS notification (
    id            BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY NOT NULL,
    header VARCHAR (50) NOT NULL,
    summary TEXT,
    category notification_category NOT NULL ,
    seen    boolean,
    user_id       VARCHAR (54) NOT NULL,
    updated_at    TIMESTAMP(6) WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_at    TIMESTAMP(6) WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by    CHARACTER VARYING(54),
    updated_by    CHARACTER VARYING(54)
)
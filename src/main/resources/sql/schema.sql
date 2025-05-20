BEGIN;

DROP TABLE IF EXISTS users,
    users_credential,
    users_confirmation,
    consultant_patient,
    admin_assignment,
    assignment,
    consultant_recommendation,
    article,
    article_recommendation,
    certification,
    search,
    roles,message,
    consultation CASCADE;

/*                                              USER TABLE                                                             */
CREATE TABLE IF NOT EXISTS users
(
    id                  BIGINT PRIMARY KEY,
    user_id             CHARACTER VARYING(54) UNIQUE  NOT NULL,
    dtype               CHARACTER VARYING(54) NOT NULL CHECK ( dtype IN ('Admin','Patient','Consultant')),
    username            jsonb,
    email               CHARACTER VARYING(54) UNIQUE NOT NULL,
    phone_number        CHARACTER VARYING(54)                  DEFAULT NULL,
    bio                 TEXT                                   DEFAULT NULL,
    profile_picture     jsonb,
    address             jsonb,
    user_type           CHARACTER VARYING(54)         NOT NULL,
    user_stage          CHARACTER VARYING(54)           NOT NULL,
    enabled             BOOLEAN                       NOT NULL DEFAULT FALSE,
    account_non_expired BOOLEAN                       NOT NULL DEFAULT TRUE,
    account_non_locked  BOOLEAN                       NOT NULL DEFAULT TRUE,
    gender              CHARACTER VARYING(54)         NOT NULL,
    role                CHARACTER VARYING(54)         NOT NULL,
    last_login          TIMESTAMP(6) WITH TIME ZONE                              DEFAULT NULL,
    updated_at          TIMESTAMP(6) WITH TIME ZONE            DEFAULT CURRENT_TIMESTAMP,
    created_at          TIMESTAMP(6) WITH TIME ZONE            DEFAULT CURRENT_TIMESTAMP,
    created_by          BIGINT                        NOT NULL,
    updated_by          BIGINT                        NOT NULL,

    /*Patient*/
    medical_categories jsonb,
    recommendation_id  BIGINT,
    /*Consultant*/
    specialization jsonb ,
    certified      BOOLEAN               DEFAULT FALSE,
    certificate_id BIGINT,
    /*Admin*/
    assignment_id BIGINT DEFAULT NULL,
    /*Credential embed*/
    user_credential jsonb

);


/*                                             PATIENT TABLES                                                          */
CREATE TABLE IF NOT EXISTS users_credential
(

);

CREATE TABLE IF NOT EXISTS users_confirmation
(
    id           BIGINT PRIMARY KEY UNIQUE,
    user_id      BIGINT                NOT NULL,
    expiry      TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    key          CHARACTER VARYING(100) NOT NULL,
    updated_at   TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_at   TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by   BIGINT                NOT NULL,
    updated_by   BIGINT                NOT NULL
);



CREATE TABLE IF NOT EXISTS certification
(
    id               BIGINT PRIMARY KEY,
    consultant_id    BIGINT                       NOT NULL,
    assignment_id    BIGINT                       NOT NULL,
    file_name        CHARACTER VARYING(255)        NOT NULL,
    file_url         CHARACTER VARYING(255)        NOT NULL,
    certificate_type CHARACTER VARYING(30)        NOT NULL,
    confirmed        BOOLEAN                     DEFAULT TRUE,
    updated_at       TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_at       TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by       BIGINT                       NOT NULL,
    updated_by       BIGINT                       NOT NULL

);

CREATE TABLE IF NOT EXISTS assignment
(
    id           BIGINT PRIMARY KEY,
    admin_id     BIGINT,
    updated_at   TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_at   TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by   BIGINT NOT NULL,
    updated_by   BIGINT NOT NULL
);

CREATE TABLE IF NOT EXISTS admin_assignment
(
    admin_id      BIGINT NOT NULL,
    assignment_id BIGINT NOT NULL
);


CREATE TABLE IF NOT EXISTS search
(
    id           BIGINT PRIMARY KEY,
    query        CHARACTER VARYING(30) NOT NULL,
    owner_id     BIGINT                NOT NULL,
    updated_at   TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_at   TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by   BIGINT                NOT NULL,
    updated_by   BIGINT                NOT NULL
);

CREATE TABLE IF NOT EXISTS article
(
    id            BIGINT PRIMARY KEY,
    title         CHARACTER VARYING(54) NOT NULL,
    content       TEXT                  NOT NULL,
    consultant_id BIGINT                NOT NULL,
    updated_at    TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_at    TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by    BIGINT                NOT NULL,
    updated_by    BIGINT                NOT NULL

);


CREATE TABLE IF NOT EXISTS consultant_recommendation
(
    id            BIGINT PRIMARY KEY,
    patient_id    BIGINT                       NOT NULL,
    consultant_id BIGINT                       NOT NULL,
    updated_at    TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_at    TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by    BIGINT                       NOT NULL,
    updated_by    BIGINT                       NOT NULL
);

CREATE TABLE IF NOT EXISTS article_recommendation
(
    id           BIGINT PRIMARY KEY,
    patient_id   BIGINT NOT NULL,
    article_id   BIGINT NOT NULL,
    updated_at   TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_at   TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by   BIGINT NOT NULL,
    updated_by   BIGINT NOT NULL
);

CREATE TABLE IF NOT EXISTS consultant_patient
(
    consultant_id BIGINT NOT NULL,
    patient_id    BIGINT NOT NULL
);

CREATE TABLE IF NOT EXISTS consultation
(
    id                BIGINT PRIMARY KEY,
    diagnosis         TEXT,
    consultation_time TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    consultation_id   CHARACTER VARYING(54),
    patient_id        BIGINT NOT NULL,
    consultant_id     BIGINT NOT NULL,
    accepted          BOOLEAN                     DEFAULT FALSE,
    status            CHARACTER VARYING(10)       DEFAULT 'NOT_STARTED',
    updated_at        TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_at        TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by        BIGINT NOT NULL,
    updated_by        BIGINT NOT NULL
);

CREATE TABLE IF NOT EXISTS message
(
    id              BIGINT PRIMARY KEY,
    sender_id       CHARACTER VARYING(54),
    receiver_id     CHARACTER VARYING(54),
    consultation_id CHARACTER VARYING(54),
    content         TEXT,
    timestamp       TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    delivered       BOOLEAN                     DEFAULT FALSE,
    updated_at      TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_at      TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by      BIGINT NOT NULL,
    updated_by      BIGINT NOT NULL

);

ALTER TABLE IF EXISTS users
    ADD FOREIGN KEY (created_by) REFERENCES users (id) MATCH SIMPLE,
    ADD FOREIGN KEY (updated_by) REFERENCES users (id) MATCH SIMPLE,
    ADD FOREIGN KEY (certificate_id) REFERENCES certification (id) MATCH SIMPLE,
    ADD FOREIGN KEY (recommendation_id) REFERENCES consultant_recommendation (id) MATCH SIMPLE,
    ADD FOREIGN KEY (assignment_id) REFERENCES assignment (id) MATCH SIMPLE;

ALTER TABLE IF EXISTS users_confirmation
    ADD FOREIGN KEY (user_id) REFERENCES users (id),
    ADD FOREIGN KEY (created_by) REFERENCES users (id),
    ADD FOREIGN KEY (updated_by) REFERENCES users (id);


ALTER TABLE IF EXISTS certification
    ADD FOREIGN KEY (assignment_id) REFERENCES assignment (id),
    ADD FOREIGN KEY (consultant_id) REFERENCES users (id),
    ADD FOREIGN KEY (created_by) REFERENCES users (id),
    ADD FOREIGN KEY (updated_by) REFERENCES users (id);

ALTER TABLE IF EXISTS assignment
    ADD FOREIGN KEY (admin_id) REFERENCES users (id),
    ADD FOREIGN KEY (created_by) REFERENCES users (id),
    ADD FOREIGN KEY (updated_by) REFERENCES users (id);

ALTER TABLE IF EXISTS search
    ADD FOREIGN KEY (owner_id) REFERENCES users (id),
    ADD FOREIGN KEY (created_by) REFERENCES users (id),
    ADD FOREIGN KEY (updated_by) REFERENCES users (id);

ALTER TABLE IF EXISTS article
    ADD FOREIGN KEY (consultant_id) references users (id),
    ADD FOREIGN KEY (created_by) references users (id),
    ADD FOREIGN KEY (updated_by) references users (id);

ALTER TABLE IF EXISTS consultant_recommendation
    ADD FOREIGN KEY (consultant_id) REFERENCES users (id),
    ADD FOREIGN KEY (patient_id) REFERENCES users (id),
    ADD FOREIGN KEY (created_by) REFERENCES users (id),
    ADD FOREIGN KEY (updated_by) REFERENCES users (id);

ALTER TABLE IF EXISTS article_recommendation
    ADD FOREIGN KEY (article_id) REFERENCES users (id),
    ADD FOREIGN KEY (patient_id) REFERENCES users (id),
    ADD FOREIGN KEY (created_by) REFERENCES users (id),
    ADD FOREIGN KEY (updated_by) REFERENCES users (id);

ALTER TABLE IF EXISTS consultant_patient
    ADD FOREIGN KEY (patient_id) REFERENCES users (id),
    ADD FOREIGN KEY (consultant_id) REFERENCES users (id);

ALTER TABLE IF EXISTS consultation
    ADD FOREIGN KEY (patient_id) REFERENCES users (id),
    ADD FOREIGN KEY (consultant_id) REFERENCES users (id);

CREATE SEQUENCE IF NOT EXISTS primary_key_seq;
END;






BEGIN;

DROP TABLE IF EXISTS users,
    users_credential,
    users_confirmation,
    consultant,
    consultant_patient,
    admin,
    admin_assignment,
    assignment,
    consultant_recommendation,
    article,
    article_recommendation,
    patient,
    certification,
    search,
    roles,message,
    consultation CASCADE;

/*                                              USER TABLE                                                             */
CREATE TABLE IF NOT EXISTS users
(
    id                  BIGINT PRIMARY KEY,
    reference_id        CHARACTER VARYING(54) UNIQUE  NOT NULL,
    user_id             CHARACTER VARYING(54) UNIQUE  NOT NULL,
    first_name          CHARACTER VARYING(54)         NOT NULL,
    last_name           CHARACTER VARYING(54)         NOT NULL,
    email               CHARACTER VARYING(54) UNIQUE NOT NULL,
    phone_number        CHARACTER VARYING(20)                  DEFAULT NULL,
    bio                 TEXT                                   DEFAULT NULL,
    image_url           CHARACTER VARYING(54)                  DEFAULT 'https://cdn-icons-png.flaticon.com/512/149/149071.png',
    image_name          CHARACTER VARYING(54)                  DEFAULT '149071.png',
    country             CHARACTER VARYING(54)        NOT NULL,
    state               CHARACTER VARYING(54)        NOT NULL,
    city                CHARACTER VARYING(54)        NOT NULL,
    zip_code            CHARACTER VARYING(10)         NOT NULL,
    user_type           CHARACTER VARYING(10)         NOT NULL,
    enabled             BOOLEAN                       NOT NULL DEFAULT FALSE,
    account_non_expired BOOLEAN                       NOT NULL DEFAULT TRUE,
    account_non_locked  BOOLEAN                       NOT NULL DEFAULT TRUE,
    gender              CHARACTER VARYING(10)         NOT NULL,
    role_id             BIGINT,
    last_login          TIMESTAMP                              DEFAULT NULL,
    login_attempts      INTEGER                                DEFAULT 0,
    updated_at          TIMESTAMP(6) WITH TIME ZONE            DEFAULT CURRENT_TIMESTAMP,
    created_at          TIMESTAMP(6) WITH TIME ZONE            DEFAULT CURRENT_TIMESTAMP,
    created_by          BIGINT                        NOT NULL,
    updated_by          BIGINT                        NOT NULL
);


/*                                             PATIENT TABLES                                                          */
CREATE TABLE IF NOT EXISTS users_credential
(
    id           BIGINT PRIMARY KEY,
    user_id      BIGINT                NOT NULL,
    reference_id CHARACTER VARYING(54),
    password     CHARACTER VARYING(54) NOT NULL,
    expired      BOOLEAN                     DEFAULT FALSE,
    updated_at   TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_at   TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by   BIGINT                NOT NULL,
    updated_by   BIGINT                NOT NULL
);

CREATE TABLE IF NOT EXISTS users_confirmation
(
    id           BIGINT PRIMARY KEY UNIQUE,
    reference_id CHARACTER VARYING(54),
    user_id      BIGINT                NOT NULL,
    expiry      TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    key          CHARACTER VARYING(30) NOT NULL,
    updated_at   TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_at   TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by   BIGINT                NOT NULL,
    updated_by   BIGINT                NOT NULL
);

CREATE TABLE IF NOT EXISTS patient
(
    id                 BIGINT PRIMARY KEY,
    medical_categories CHARACTER VARYING(30)[] DEFAULT '{PHYSICAL_THERAPY}',
    recommendation_id  BIGINT
);
/*                                      CONSULTANT TABLES                                                              */
CREATE TABLE IF NOT EXISTS consultant
(
    id             BIGINT PRIMARY KEY,
    specialization CHARACTER VARYING(30) NOT NULL,
    bio            TEXT                           DEFAULT NULL,
    certified      BOOLEAN               NOT NULL DEFAULT FALSE,
    certificate_id BIGINT
);


CREATE TABLE IF NOT EXISTS roles
(
    id          BIGINT PRIMARY KEY,
    name        CHARACTER VARYING(20) NOT NULL,
    permissions CHARACTER VARYING(30) NOT NULL
);


CREATE TABLE IF NOT EXISTS certification
(
    id               BIGINT PRIMARY KEY,
    reference_id     CHARACTER VARYING(54) UNIQUE NOT NULL,
    consultant_id    BIGINT                       NOT NULL,
    assignment_id    BIGINT                       NOT NULL,
    file_name        CHARACTER VARYING(30)        NOT NULL,
    file_url         CHARACTER VARYING(30)        NOT NULL,
    certificate_type CHARACTER VARYING(30)        NOT NULL,
    confirmed        BOOLEAN                     DEFAULT TRUE,
    updated_at       TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_at       TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by       BIGINT                       NOT NULL,
    updated_by       BIGINT                       NOT NULL

);


/*                                          ADMIN TABLES                                                                */


CREATE TABLE IF NOT EXISTS admin
(
    id            BIGINT PRIMARY KEY,
    assignment_id BIGINT DEFAULT NULL
);

CREATE TABLE IF NOT EXISTS assignment
(
    id           BIGINT PRIMARY KEY,
    reference_id CHARACTER VARYING(54) UNIQUE,
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
    reference_id CHARACTER VARYING(54) UNIQUE,
    updated_at   TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_at   TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by   BIGINT                NOT NULL,
    updated_by   BIGINT                NOT NULL
);

CREATE TABLE IF NOT EXISTS article
(
    id            BIGINT PRIMARY KEY,
    title         CHARACTER VARYING(30) NOT NULL,
    content       TEXT                  NOT NULL,
    consultant_id BIGINT                NOT NULL,
    reference_id  CHARACTER VARYING(54),
    updated_at    TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_at    TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by    BIGINT                NOT NULL,
    updated_by    BIGINT                NOT NULL

);


CREATE TABLE IF NOT EXISTS consultant_recommendation
(
    id            BIGINT PRIMARY KEY,
    reference_id  CHARACTER VARYING(54) UNIQUE NOT NULL,
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
    reference_id CHARACTER VARYING(54) UNIQUE,
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
    reference_id      CHARACTER VARYING(54) UNIQUE,
    diagnosis         TEXT,
    consultation_time TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    consultation_id   CHARACTER VARYING(30),
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
    reference_id    CHARACTER VARYING(54),
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
    ADD FOREIGN KEY (created_by) REFERENCES users (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE,
    ADD FOREIGN KEY (updated_by) REFERENCES users (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE,
    ADD FOREIGN KEY (role_id) REFERENCES roles (id) MATCH SIMPLE;

ALTER TABLE IF EXISTS users_confirmation
    ADD FOREIGN KEY (user_id) REFERENCES users (id),
    ADD FOREIGN KEY (created_by) REFERENCES users (id),
    ADD FOREIGN KEY (updated_by) REFERENCES users (id);

ALTER TABLE IF EXISTS users_credential
    ADD FOREIGN KEY (user_id) REFERENCES users (id),
    ADD FOREIGN KEY (created_by) REFERENCES users (id),
    ADD FOREIGN KEY (updated_by) REFERENCES users (id);

ALTER TABLE IF EXISTS patient
    ADD FOREIGN KEY (id) REFERENCES users (id) MATCH SIMPLE,
    ADD FOREIGN KEY (recommendation_id) REFERENCES consultant_recommendation (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE IF EXISTS consultant
    ADD FOREIGN KEY (id) REFERENCES users (id),
    ADD FOREIGN KEY (certificate_id) REFERENCES certification (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE;


ALTER TABLE IF EXISTS certification
    ADD FOREIGN KEY (assignment_id) REFERENCES assignment (id),
    ADD FOREIGN KEY (consultant_id) REFERENCES consultant (id),
    ADD FOREIGN KEY (created_by) REFERENCES consultant (id),
    ADD FOREIGN KEY (updated_by) REFERENCES consultant (id);

ALTER TABLE IF EXISTS admin
    ADD FOREIGN KEY (id) REFERENCES users (id),
    ADD FOREIGN KEY (assignment_id) REFERENCES assignment (id);

ALTER TABLE IF EXISTS assignment
    ADD FOREIGN KEY (admin_id) REFERENCES admin (id),
    ADD FOREIGN KEY (created_by) REFERENCES admin (id),
    ADD FOREIGN KEY (updated_by) REFERENCES admin (id);

ALTER TABLE IF EXISTS search
    ADD FOREIGN KEY (owner_id) REFERENCES users (id),
    ADD FOREIGN KEY (created_by) REFERENCES users (id),
    ADD FOREIGN KEY (updated_by) REFERENCES users (id);

ALTER TABLE IF EXISTS article
    ADD FOREIGN KEY (consultant_id) references consultant (id),
    ADD FOREIGN KEY (created_by) references consultant (id),
    ADD FOREIGN KEY (updated_by) references consultant (id);

ALTER TABLE IF EXISTS consultant_recommendation
    ADD FOREIGN KEY (consultant_id) REFERENCES consultant (id),
    ADD FOREIGN KEY (patient_id) REFERENCES patient (id),
    ADD FOREIGN KEY (created_by) REFERENCES patient (id),
    ADD FOREIGN KEY (updated_by) REFERENCES patient (id);

ALTER TABLE IF EXISTS article_recommendation
    ADD FOREIGN KEY (article_id) REFERENCES consultant (id),
    ADD FOREIGN KEY (patient_id) REFERENCES patient (id),
    ADD FOREIGN KEY (created_by) REFERENCES patient (id),
    ADD FOREIGN KEY (updated_by) REFERENCES patient (id);

ALTER TABLE IF EXISTS consultant_patient
    ADD FOREIGN KEY (patient_id) REFERENCES patient (id),
    ADD FOREIGN KEY (consultant_id) REFERENCES consultant (id);

ALTER TABLE IF EXISTS consultation
    ADD FOREIGN KEY (patient_id) REFERENCES patient (id),
    ADD FOREIGN KEY (consultant_id) REFERENCES consultant (id);

CREATE SEQUENCE IF NOT EXISTS primary_key_seq;
CREATE INDEX idx_consultant_user_name ON users USING gin (to_tsvector('english', first_name),
                                                          to_tsvector('english', last_name));
CREATE INDEX idx_consultant_specialization ON consultant USING gin (to_tsvector('english', specialization));
CREATE INDEX idx_article ON article USING gin (to_tsvector('english', title), to_tsvector('english', content));


END;






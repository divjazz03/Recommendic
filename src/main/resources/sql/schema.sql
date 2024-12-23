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
    consultation CASCADE ;

/*                                              USER TABLE                                                             */
CREATE TABLE IF NOT EXISTS users(
    id                  BIGINT PRIMARY KEY ,
    reference_id        CHARACTER VARYING(255)                   NOT NULL,
    user_id             CHARACTER VARYING(255)                  NOT NULL ,
    first_name          CHARACTER VARYING(50)  NOT NULL,
    last_name           CHARACTER VARYING(50)  NOT NULL,
    email               CHARACTER VARYING(100) NOT NULL,
    phone_number        CHARACTER VARYING(20)           DEFAULT NULL,
    bio                 TEXT                            DEFAULT NULL,
    image_url           CHARACTER VARYING(255)          DEFAULT 'https://cdn-icons-png.flaticon.com/512/149/149071.png',
    image_name          CHARACTER VARYING(255)          DEFAULT '149071.png',
    country             CHARACTER VARYING(100) NOT NULL,
    state               CHARACTER VARYING(100) NOT NULL, CONSTRAINT uq_admin_email UNIQUE (email),
    city                CHARACTER VARYING(100) NOT NULL,
    zip_code            CHARACTER VARYING(10)  NOT NULL,
    user_type           CHARACTER VARYING(10)  NOT NULL,
    enabled             BOOLEAN                NOT NULL DEFAULT FALSE,
    account_non_expired BOOLEAN                NOT NULL DEFAULT TRUE,
    account_non_locked  BOOLEAN                NOT NULL DEFAULT TRUE,
    gender              CHARACTER VARYING(10)  NOT NULL,
    role_id             BIGINT               ,
    last_login          TIMESTAMP                       DEFAULT NULL,
    login_attempts      INTEGER                         DEFAULT 0,
    updated_at          TIMESTAMP(6) WITH TIME ZONE     DEFAULT CURRENT_TIMESTAMP,
    created_at          TIMESTAMP(6) WITH TIME ZONE     DEFAULT CURRENT_TIMESTAMP,
    created_by          BIGINT                 NOT NULL,
    updated_by          BIGINT                 NOT NULL
--     CONSTRAINT uq_user_email UNIQUE (email),
--     CONSTRAINT uq_user_id UNIQUE (id),
--     CONSTRAINT uq_user_reference_id UNIQUE (reference_id)
);


/*                                             PATIENT TABLES                                                          */
CREATE TABLE IF NOT EXISTS users_credential
(
    id           BIGINT PRIMARY KEY,
    user_id   BIGINT                 NOT NULL,
    reference_id CHARACTER VARYING(255),
    password     CHARACTER VARYING(255) NOT NULL,
    expired      BOOLEAN DEFAULT FALSE,
    updated_at   TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_at   TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by   BIGINT                 NOT NULL,
    updated_by   BIGINT                 NOT NULL
);

CREATE TABLE IF NOT EXISTS users_confirmation
(
    id           BIGINT PRIMARY KEY,
    reference_id CHARACTER VARYING(255),
    user_id   BIGINT                 NOT NULL,
    expired      BOOLEAN                     DEFAULT FALSE,
    key          CHARACTER VARYING(255) NOT NULL,
    updated_at   TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_at   TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by   BIGINT                 NOT NULL,
    updated_by   BIGINT                 NOT NULL
    -- CONSTRAINT uq_patient_confirmation_key UNIQUE (key)
);

CREATE TABLE IF NOT EXISTS patient
(
    id                  BIGINT PRIMARY KEY,
    medical_categories  CHARACTER VARYING(30)[]         DEFAULT '{PHYSICAL_THERAPY}',
    recommendation_id   BIGINT
);
/*                                      CONSULTANT TABLES                                                              */
CREATE TABLE IF NOT EXISTS consultant
(
    id                  BIGINT PRIMARY KEY,
    specialization      CHARACTER VARYING(30)  NOT NULL,
    bio                 TEXT                   DEFAULT NULL,
    certified           BOOLEAN                NOT NULL DEFAULT FALSE,
    certificate_id      BIGINT
);


CREATE TABLE IF NOT EXISTS roles(
    id              BIGINT PRIMARY KEY ,
    name            CHARACTER VARYING(20)   NOT NULL ,
    permissions     CHARACTER VARYING(255)  NOT NULL
);


CREATE TABLE IF NOT EXISTS certification
(
    id               BIGINT PRIMARY KEY,
    reference_id     CHARACTER VARYING(255)                   NOT NULL,
    consultant_id    BIGINT                 NOT NULL,
    assignment_id    BIGINT                 NOT NULL,
    file_name        CHARACTER VARYING(255) NOT NULL,
    file_url         CHARACTER VARYING(255) NOT NULL,
    certificate_type CHARACTER VARYING(255) NOT NULL,
    confirmed        BOOLEAN                     DEFAULT TRUE,
    updated_at       TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_at       TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by       BIGINT                 NOT NULL,
    updated_by       BIGINT                 NOT NULL

);


/*                                          ADMIN TABLES                                                                */


CREATE TABLE IF NOT EXISTS admin
(
    id                  BIGINT PRIMARY KEY,
    assignment_id       BIGINT DEFAULT NULL
);

CREATE TABLE IF NOT EXISTS assignment
(
    id           BIGINT PRIMARY KEY,
    reference_id CHARACTER VARYING(255),
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
    query        CHARACTER VARYING(255) NOT NULL,
    owner_id     BIGINT                 NOT NULL,
    reference_id CHARACTER VARYING(255),
    updated_at   TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_at   TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by   BIGINT                 NOT NULL,
    updated_by   BIGINT                 NOT NULL
);

CREATE TABLE IF NOT EXISTS article
(
    id              BIGINT PRIMARY KEY ,
    title           CHARACTER VARYING(255)  NOT NULL ,
    content         TEXT                    NOT NULL ,
    consultant_id   BIGINT                  NOT NULL ,
    reference_id CHARACTER VARYING(255),
    updated_at   TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_at   TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by   BIGINT                 NOT NULL,
    updated_by   BIGINT                 NOT NULL

);


CREATE TABLE IF NOT EXISTS consultant_recommendation
(
    id            BIGINT PRIMARY KEY,
    reference_id  CHARACTER VARYING(255),
    patient_id    BIGINT NOT NULL,
    consultant_id BIGINT NOT NULL,
    updated_at    TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_at    TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by    BIGINT NOT NULL,
    updated_by    BIGINT NOT NULL
    -- CONSTRAINT uq_recommendation_id UNIQUE (id)
);

CREATE TABLE IF NOT EXISTS article_recommendation
(
    id              BIGINT PRIMARY KEY,
    reference_id    CHARACTER VARYING(255),
    patient_id      BIGINT NOT NULL ,
    article_id      BIGINT NOT NULL ,
    updated_at    TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_at    TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by    BIGINT NOT NULL,
    updated_by    BIGINT NOT NULL
    -- CONSTRAINT uq_recommendation_id UNIQUE (id)
);

CREATE TABLE IF NOT EXISTS consultant_patient
(
    consultant_id BIGINT NOT NULL,
    patient_id    BIGINT NOT NULL
);

CREATE TABLE IF NOT EXISTS consultation(
    id                  BIGINT PRIMARY KEY,
    reference_id        CHARACTER VARYING(255),
    diagnosis           TEXT,
    consultation_time   TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    consultation_id     CHARACTER VARYING(255),
    patient_id          BIGINT NOT NULL,
    consultant_id       BIGINT NOT NULL,
    accepted            BOOLEAN DEFAULT FALSE,
    status              CHARACTER VARYING(10) DEFAULT 'NOT_STARTED',
    updated_at          TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_at          TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by          BIGINT NOT NULL,
    updated_by          BIGINT NOT NULL
   -- CONSTRAINT uq_consultation_id UNIQUE (id)
);

CREATE TABLE IF NOT EXISTS message (
    id              BIGINT PRIMARY KEY ,
    reference_id    CHARACTER VARYING(255),
    sender_id       CHARACTER VARYING(255),
    receiver_id     CHARACTER VARYING(255),
    consultation_id CHARACTER VARYING(255),
    content         TEXT,
    timestamp       CHARACTER VARYING(30),
    delivered          BOOLEAN DEFAULT FALSE,
    updated_at    TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_at    TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by    BIGINT NOT NULL,
    updated_by    BIGINT NOT NULL

);

ALTER TABLE IF EXISTS users
    ADD FOREIGN KEY (created_by) REFERENCES users (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE,
    ADD FOREIGN KEY (updated_by) REFERENCES users (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE,
    ADD FOREIGN KEY (role_id) REFERENCES  roles (id) MATCH SIMPLE ;

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
CREATE INDEX idx_consultant_user_name ON users USING gin (to_tsvector('english',first_name), to_tsvector('english', last_name));
CREATE INDEX idx_consultant_specialization ON consultant USING gin (to_tsvector('english',specialization));
CREATE INDEX idx_article ON article USING gin (to_tsvector('english', title), to_tsvector('english', content));


END;






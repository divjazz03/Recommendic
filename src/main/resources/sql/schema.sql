BEGIN;
DROP TABLE IF EXISTS patient,
    patient_credential,
    patient_confirmation,
    consultant, consultant_credential, consultant_confirmation, consultant_recommendation,consultant_patient,
    admin, admin_credential, admin_confirmation,
    admin_assignment,consultant_certification,
    assignment, recommendation, certification, search;


/*                                             PATIENT TABLES                                                          */
CREATE TABLE IF NOT EXISTS patient
(
    id                  BIGINT PRIMARY KEY,
    reference_id        UUID                   NOT NULL,
    first_name          CHARACTER VARYING(50)  NOT NULL,
    last_name           CHARACTER VARYING(50)  NOT NULL,
    email               CHARACTER VARYING(100) NOT NULL,
    phone_number        CHARACTER VARYING(20)           DEFAULT NULL,
    bio                 TEXT                            DEFAULT NULL,
    image_url           CHARACTER VARYING(255)          DEFAULT 'https://cdn-icons-png.flaticon.com/512/149/149071.png',
    image_name          CHARACTER VARYING(255)          DEFAULT '149071.png',
    country             CHARACTER VARYING(100)  NOT NULL,
    state               CHARACTER VARYING(100)  NOT NULL,
    city                CHARACTER VARYING(100)  NOT NULL,
    zip_code            CHARACTER VARYING(10)  NOT NULL,
    enabled             BOOLEAN                NOT NULL DEFAULT FALSE,
    account_non_expired BOOLEAN                NOT NULL DEFAULT TRUE,
    account_non_locked  BOOLEAN                NOT NULL DEFAULT TRUE,
    gender              CHARACTER VARYING(10)  NOT NULL,
    medical_categories  CHARACTER VARYING(30)[]         DEFAULT '{PHYSICAL_THERAPY}',
    recommendation_id   BIGINT,
    updated_at          TIMESTAMP(6) WITH TIME ZONE     DEFAULT CURRENT_TIMESTAMP,
    created_at          TIMESTAMP(6) WITH TIME ZONE     DEFAULT CURRENT_TIMESTAMP,
    created_by          BIGINT                 NOT NULL,
    updated_by          BIGINT                 NOT NULL,
    CONSTRAINT uq_patient_email UNIQUE (email),
    CONSTRAINT uq_patient_id UNIQUE (id),
    CONSTRAINT uq_patient_reference_id UNIQUE (reference_id)
);

CREATE TABLE IF NOT EXISTS patient_credential
(
    id           BIGINT PRIMARY KEY,
    patient_id   BIGINT                 NOT NULL,
    reference_id UUID,
    password     CHARACTER VARYING(255) NOT NULL,
    updated_at   TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_at   TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by   BIGINT                 NOT NULL,
    updated_by   BIGINT                 NOT NULL
);

CREATE TABLE IF NOT EXISTS patient_confirmation
(
    id           BIGINT PRIMARY KEY,
    reference_id UUID,
    patient_id   BIGINT                 NOT NULL,
    expired      BOOLEAN                     DEFAULT FALSE,
    key          CHARACTER VARYING(255) NOT NULL,
    updated_at   TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_at   TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by   BIGINT                 NOT NULL,
    updated_by   BIGINT                 NOT NULL,
    CONSTRAINT uq_patient_confirmation_key UNIQUE (key)
);
/*                                      CONSULTANT TABLES                                                              */
CREATE TABLE IF NOT EXISTS consultant
(
    id                  BIGINT PRIMARY KEY,
    reference_id        UUID                   NOT NULL,
    first_name          CHARACTER VARYING(50)  NOT NULL,
    last_name           CHARACTER VARYING(50)  NOT NULL,
    email               CHARACTER VARYING(100) NOT NULL,
    phone_number        CHARACTER VARYING(20)           DEFAULT NULL,
    bio                 TEXT                            DEFAULT NULL,
    image_url           CHARACTER VARYING(255)          DEFAULT 'https://cdn-icons-png.flaticon.com/512/149/149071.png',
    image_name          CHARACTER VARYING(255)          DEFAULT '149071.png',
    country             CHARACTER VARYING(100)  NOT NULL,
    state               CHARACTER VARYING(100)  NOT NULL,
    city                CHARACTER VARYING(100)  NOT NULL,
    zip_code            CHARACTER VARYING(10)  NOT NULL,
    enabled             BOOLEAN                NOT NULL DEFAULT FALSE,
    account_non_expired BOOLEAN                NOT NULL DEFAULT TRUE,
    account_non_locked  BOOLEAN                NOT NULL DEFAULT TRUE,
    gender              CHARACTER VARYING(10)  NOT NULL,
    specialization      CHARACTER VARYING(30)  NOT NULL,
    certified           BOOLEAN                NOT NULL DEFAULT FALSE,
    certificate_id      BIGINT,
    updated_at          TIMESTAMP(6) WITH TIME ZONE     DEFAULT CURRENT_TIMESTAMP,
    created_at          TIMESTAMP(6) WITH TIME ZONE     DEFAULT CURRENT_TIMESTAMP,
    created_by          BIGINT                 NOT NULL,
    updated_by          BIGINT                 NOT NULL,
    CONSTRAINT uq_consultant_email UNIQUE (email),
    CONSTRAINT uq_consultant_id UNIQUE (id),
    CONSTRAINT uq_consultant_reference_id UNIQUE (reference_id)

);

CREATE TABLE IF NOT EXISTS consultant_recommendation
(
    consultant_id     BIGINT NOT NULL,
    recommendation_id BIGINT NOT NULL
);

CREATE TABLE IF NOT EXISTS consultant_credential
(
    id            BIGINT PRIMARY KEY,
    reference_id  UUID,
    consultant_id BIGINT                 NOT NULL,
    password      CHARACTER VARYING(255) NOT NULL,
    updated_at    TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_at    TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by    BIGINT                 NOT NULL,
    updated_by    BIGINT                 NOT NULL
);

CREATE TABLE IF NOT EXISTS consultant_confirmation
(
    id            BIGINT PRIMARY KEY,
    reference_id  UUID,
    consultant_id BIGINT                 NOT NULL,
    expired       BOOLEAN                     DEFAULT FALSE,
    key           CHARACTER VARYING(255) NOT NULL,
    updated_at    TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_at    TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by    BIGINT                 NOT NULL,
    updated_by    BIGINT                 NOT NULL,
    CONSTRAINT uq_consultant_confirmation_key UNIQUE (key)

);

CREATE TABLE IF NOT EXISTS certification
(
    id               BIGINT PRIMARY KEY,
    reference_id     UUID                   NOT NULL,
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
    reference_id        UUID                   NOT NULL,
    first_name          CHARACTER VARYING(50)  NOT NULL,
    last_name           CHARACTER VARYING(50)  NOT NULL,
    email               CHARACTER VARYING(100) NOT NULL,
    phone_number        CHARACTER VARYING(20)           DEFAULT NULL,
    bio                 TEXT                            DEFAULT NULL,
    image_url           CHARACTER VARYING(255)          DEFAULT 'https://cdn-icons-png.flaticon.com/512/149/149071.png',
    image_name          CHARACTER VARYING(255)          DEFAULT '149071.png',
    country             CHARACTER VARYING(100)  NOT NULL,
    state               CHARACTER VARYING(100)  NOT NULL,
    city                CHARACTER VARYING(100)  NOT NULL,
    zip_code            CHARACTER VARYING(10)  NOT NULL,
    enabled             BOOLEAN                NOT NULL DEFAULT FALSE,
    account_non_expired BOOLEAN                NOT NULL DEFAULT TRUE,
    account_non_locked  BOOLEAN                NOT NULL DEFAULT TRUE,
    gender              CHARACTER VARYING(10)  NOT NULL,
    updated_at          TIMESTAMP(6) WITH TIME ZONE     DEFAULT CURRENT_TIMESTAMP,
    created_at          TIMESTAMP(6) WITH TIME ZONE     DEFAULT CURRENT_TIMESTAMP,
    created_by          BIGINT                 NOT NULL,
    updated_by          BIGINT                 NOT NULL,
    CONSTRAINT uq_admin_email UNIQUE (email),
    CONSTRAINT uq_admin_id UNIQUE (id),
    CONSTRAINT uq_admin_reference_id UNIQUE (reference_id)

);

CREATE TABLE IF NOT EXISTS assignment
(
    id           BIGINT PRIMARY KEY,
    reference_id UUID,
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

CREATE TABLE IF NOT EXISTS admin_confirmation
(
    id           BIGINT PRIMARY KEY,
    reference_id UUID,
    admin_id     BIGINT                 NOT NULL,
    expired      BOOLEAN                     DEFAULT FALSE,
    key          CHARACTER VARYING(255) NOT NULL,
    updated_at   TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_at   TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by   BIGINT                 NOT NULL,
    updated_by   BIGINT                 NOT NULL,
    CONSTRAINT uq_admin_confirmation_key UNIQUE (key)

);

CREATE TABLE IF NOT EXISTS admin_credential
(
    id           BIGINT PRIMARY KEY,
    reference_id UUID,
    admin_id     BIGINT                 NOT NULL,
    expired      BOOLEAN                NOT NULL DEFAULT FALSE,
    password     CHARACTER VARYING(255) NOT NULL,
    updated_at   TIMESTAMP(6) WITH TIME ZONE     DEFAULT CURRENT_TIMESTAMP,
    created_at   TIMESTAMP(6) WITH TIME ZONE     DEFAULT CURRENT_TIMESTAMP,
    created_by   BIGINT                 NOT NULL,
    updated_by   BIGINT                 NOT NULL
);



CREATE TABLE IF NOT EXISTS search
(
    id           BIGINT PRIMARY KEY,
    query        CHARACTER VARYING(255) NOT NULL,
    owner_id     BIGINT                 NOT NULL,
    reference_id UUID,
    updated_at   TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_at   TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by   BIGINT                 NOT NULL,
    updated_by   BIGINT                 NOT NULL
);

CREATE TABLE IF NOT EXISTS recommendation
(
    id            BIGINT PRIMARY KEY,
    reference_id  UUID,
    patient_id    BIGINT NOT NULL,
    consultant_id BIGINT NOT NULL,
    updated_at    TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_at    TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by    BIGINT NOT NULL,
    updated_by    BIGINT NOT NULL,
    CONSTRAINT uq_recommendation_id UNIQUE (id)
);

CREATE TABLE IF NOT EXISTS consultant_patient
(
    consultant_id BIGINT NOT NULL,
    patient_id    BIGINT NOT NULL
);

ALTER TABLE IF EXISTS patient
    ADD FOREIGN KEY (created_by) REFERENCES patient (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE,
    ADD FOREIGN KEY (updated_by) REFERENCES patient (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE,
    ADD FOREIGN KEY (recommendation_id) REFERENCES recommendation (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE IF EXISTS patient_credential
    ADD FOREIGN KEY (patient_id) REFERENCES patient (id),
    ADD FOREIGN KEY (created_by) REFERENCES patient (id),
    ADD FOREIGN KEY (updated_by) REFERENCES patient (id);

ALTER TABLE IF EXISTS patient_confirmation
    ADD FOREIGN KEY (patient_id) REFERENCES patient (id),
    ADD FOREIGN KEY (created_by) REFERENCES patient (id),
    ADD FOREIGN KEY (updated_by) REFERENCES patient (id);

ALTER TABLE IF EXISTS consultant
    ADD FOREIGN KEY (created_by) REFERENCES patient (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE,
    ADD FOREIGN KEY (updated_by) REFERENCES patient (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE,
    ADD FOREIGN KEY (certificate_id) REFERENCES certification (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE IF EXISTS consultant_recommendation
    ADD FOREIGN KEY (recommendation_id) REFERENCES recommendation (id),
    ADD FOREIGN KEY (consultant_id) REFERENCES consultant (id);

ALTER TABLE IF EXISTS consultant_credential
    ADD FOREIGN KEY (consultant_id) REFERENCES consultant (id),
    ADD FOREIGN KEY (created_by) REFERENCES consultant (id),
    ADD FOREIGN KEY (updated_by) REFERENCES consultant (id);

ALTER TABLE IF EXISTS consultant_confirmation
    ADD FOREIGN KEY (consultant_id) REFERENCES consultant (id),
    ADD FOREIGN KEY (created_by) REFERENCES consultant (id),
    ADD FOREIGN KEY (updated_by) REFERENCES consultant (id);

ALTER TABLE IF EXISTS certification
    ADD FOREIGN KEY (assignment_id) REFERENCES assignment (id),
    ADD FOREIGN KEY (consultant_id) REFERENCES consultant (id),
    ADD FOREIGN KEY (created_by) REFERENCES consultant (id),
    ADD FOREIGN KEY (updated_by) REFERENCES consultant (id);

ALTER TABLE IF EXISTS admin
    ADD FOREIGN KEY (created_by) REFERENCES admin (id) ON UPDATE CASCADE ON DELETE CASCADE,
    ADD FOREIGN KEY (updated_by) REFERENCES admin (id) ON UPDATE CASCADE ON DELETE CASCADE;


ALTER TABLE IF EXISTS assignment
    ADD FOREIGN KEY (created_by) REFERENCES admin (id),
    ADD FOREIGN KEY (updated_by) REFERENCES admin (id);

ALTER TABLE IF EXISTS admin_confirmation
    ADD FOREIGN KEY (admin_id) REFERENCES admin (id),
    ADD FOREIGN KEY (created_by) REFERENCES admin (id),
    ADD FOREIGN KEY (updated_by) REFERENCES admin (id);

ALTER TABLE IF EXISTS admin_credential
    ADD FOREIGN KEY (admin_id) REFERENCES admin (id),
    ADD FOREIGN KEY (created_by) REFERENCES admin (id),
    ADD FOREIGN KEY (updated_by) REFERENCES admin (id);

ALTER TABLE IF EXISTS search
    ADD FOREIGN KEY (owner_id) REFERENCES patient (id),
    ADD FOREIGN KEY (created_by) REFERENCES patient (id),
    ADD FOREIGN KEY (updated_by) REFERENCES patient (id);


ALTER TABLE IF EXISTS recommendation
    ADD FOREIGN KEY (consultant_id) REFERENCES consultant (id),
    ADD FOREIGN KEY (patient_id) REFERENCES patient (id),
    ADD FOREIGN KEY (created_by) REFERENCES patient (id),
    ADD FOREIGN KEY (updated_by) REFERENCES patient (id);

ALTER TABLE IF EXISTS consultant_patient
    ADD FOREIGN KEY (patient_id) REFERENCES patient (id),
    ADD FOREIGN KEY (consultant_id) REFERENCES consultant (id);

CREATE SEQUENCE IF NOT EXISTS primary_key_seq;

CREATE INDEX IF NOT EXISTS index_patient_email
    on patient (email);
CREATE INDEX IF NOT EXISTS index_patient_id
    ON patient (id);
CREATE INDEX IF NOT EXISTS index_consultant_email
    ON consultant (email);
CREATE INDEX IF NOT EXISTS index_consultant_id
    ON consultant (id);
CREATE INDEX IF NOT EXISTS index_admin_email
    ON admin (email);
CREATE INDEX IF NOT EXISTS index_admin_id
    ON admin (id);


END;






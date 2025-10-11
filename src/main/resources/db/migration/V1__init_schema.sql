BEGIN;
/* Schemas include public patient consultant admin*/
DROP TABLE IF EXISTS
    patient,
    consultant,
    admin,
    users_credential,
    users_confirmation,
    consultant_patient,
    admin_assignment,
    assignment,
    consultant_recommendation,
    article,
    comment,
    article_recommendation,
    certification,
    consultant_profiles,
    patient_profiles,
    search,
    message, medical_category, role CASCADE;
DROP TYPE IF EXISTS article_search_result, article_status_enum, message_search_result, user_security_data CASCADE;

DROP INDEX IF EXISTS
    article_search_idx,
    idx_consultant_email,
    idx_consultant_user_id,
    idx_search_owner_id,
    idx_patient_email,
    idx_patient_user_id,
    message_search_idx CASCADE;

CREATE EXTENSION IF NOT EXISTS pg_trgm;
CREATE EXTENSION IF NOT EXISTS unaccent;

CREATE TABLE IF NOT EXISTS role
(
    id          BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    permissions TEXT[],
    name        TEXT UNIQUE NOT NULL
);

INSERT INTO role (name)
VALUES ('ROLE_ADMIN'),
       ('ROLE_PATIENT'),
       ('ROLE_CONSULTANT'),
       ('ROLE_SUPER_ADMIN'),
       ('ROLE_SYSTEM');

CREATE TABLE IF NOT EXISTS medical_category
(
    id          BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    name        TEXT UNIQUE NOT NULL,
    description TEXT NOT NULL
);

CREATE INDEX idx_medical_category_name ON medical_category(name);
INSERT INTO medical_category (name, description)
VALUES ('pediatrician', 'Dealing with care and basic treatment of children'),
       ('cardiology','Dealing with treatment of the heart'),
       ('oncology','Dealing with treatment of Cancer'),
       ('dermatology','Dealing with treatment of the skin'),
       ('orthopedic surgery','Dealing with surgery relating to the bones'),
       ('neurosurgery','Dealing with surgery relating to the brain'),
       ('cardiovascular surgery','Dealing with surgery relating to the heart'),
       ('gynecology','Dealing with women''s reproductive health'),
       ('psychiatry','Dealing with mental health disorders'),
       ('dentistry','Dealing with oral health'),
       ('ophthalmology','Dealing with eye care'),
       ('physical therapy','Dealing with recovery of patients rom injuries or surgeries');

/*                                              USER TABLE                                                             */
CREATE TABLE IF NOT EXISTS admin
(
    id                  BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    user_uuid           UUID DEFAULT gen_random_uuid() UNIQUE,
    user_id             TEXT GENERATED ALWAYS AS ( 'ADM-' || user_uuid ) STORED ,
    email               TEXT UNIQUE NOT NULL,
    user_type           TEXT        NOT NULL,
    user_stage          TEXT        NOT NULL,
    enabled             BOOLEAN     NOT NULL DEFAULT FALSE,
    account_non_expired BOOLEAN     NOT NULL DEFAULT TRUE,
    account_non_locked  BOOLEAN     NOT NULL DEFAULT TRUE,
    gender              TEXT        NOT NULL,
    role                BIGINT REFERENCES role (id),
    last_login          TIMESTAMP            DEFAULT NULL,
    updated_at          TIMESTAMP            DEFAULT CURRENT_TIMESTAMP,
    created_at          TIMESTAMP            DEFAULT CURRENT_TIMESTAMP,
    created_by          TEXT,
    updated_by          TEXT,
    /*Credential embed*/
    user_credential     jsonb

);
/*                                             PATIENT                                                          */
CREATE TABLE IF NOT EXISTS patient
(
    id                  BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    user_uuid           UUID DEFAULT gen_random_uuid() UNIQUE,
    user_id             TEXT GENERATED ALWAYS AS ( 'PT-' || user_uuid ) STORED ,
    email               TEXT UNIQUE NOT NULL,
    user_type           TEXT        NOT NULL,
    user_stage          TEXT        NOT NULL,
    enabled             BOOLEAN     NOT NULL DEFAULT FALSE,
    account_non_expired BOOLEAN     NOT NULL DEFAULT TRUE,
    account_non_locked  BOOLEAN     NOT NULL DEFAULT TRUE,
    gender              TEXT        NOT NULL,
    role                BIGINT REFERENCES role(id),
    last_login          TIMESTAMP            DEFAULT NULL,
    updated_at          TIMESTAMP            DEFAULT CURRENT_TIMESTAMP,
    created_at          TIMESTAMP            DEFAULT CURRENT_TIMESTAMP,
    created_by          TEXT,
    updated_by          TEXT,
    recommendation_id   BIGINT,
    user_credential     jsonb

);

CREATE TABLE IF NOT EXISTS patient_medical_category (
    patient_id BIGINT REFERENCES patient(id),
    medical_category_id BIGINT REFERENCES medical_category(id)
);

CREATE TABLE IF NOT EXISTS consultant
(
    id                  BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    user_uuid           UUID DEFAULT gen_random_uuid() UNIQUE,
    user_id             TEXT GENERATED ALWAYS AS ( 'CST-' || user_uuid ) STORED ,
    email               TEXT UNIQUE NOT NULL,
    user_type           TEXT        NOT NULL,
    user_stage          TEXT        NOT NULL,
    enabled             BOOLEAN     NOT NULL DEFAULT FALSE,
    account_non_expired BOOLEAN     NOT NULL DEFAULT TRUE,
    account_non_locked  BOOLEAN     NOT NULL DEFAULT TRUE,
    gender              TEXT        NOT NULL,
    role                BIGINT REFERENCES role (id),
    last_login          TIMESTAMP            DEFAULT NULL,
    updated_at          TIMESTAMP            DEFAULT CURRENT_TIMESTAMP,
    created_at          TIMESTAMP            DEFAULT CURRENT_TIMESTAMP,
    created_by          TEXT,
    updated_by          TEXT,
    specialization      BIGINT REFERENCES medical_category (id),
    certified           BOOLEAN              DEFAULT FALSE,
    user_credential     jsonb

);

CREATE TABLE IF NOT EXISTS patient_profiles
(
    id              BIGINT PRIMARY KEY REFERENCES patient (id) ON DELETE CASCADE ,
    profile_picture jsonb,
    address         jsonb,
    phone_number    TEXT,
    date_of_birth   DATE,
    username        jsonb,
    updated_at      TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP,
    created_at      TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP,
    search_vector   tsvector GENERATED ALWAYS AS (
                            setweight(to_tsvector('english', coalesce(username ->> 'first_name', '')), 'A') ||
                            setweight(to_tsvector('english', coalesce(username ->> 'last_name', '')), 'B') ) STORED,
    created_by      TEXT NOT NULL,
    updated_by      TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS consultant_profiles
(
    id              BIGINT PRIMARY KEY REFERENCES consultant (id) ON DELETE CASCADE ,
    profile_picture jsonb,
    address         jsonb,
    bio             TEXT         DEFAULT NULL,
    phone_number    TEXT,
    date_of_birth DATE,
    username        jsonb,
    location        TEXT,
    experience      INTEGER,
    title           TEXT,
    languages       TEXT[],
    updated_at      TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP,
    created_at      TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP,
    search_vector   tsvector GENERATED ALWAYS AS (
                            setweight(to_tsvector('english', coalesce(username ->> 'first_name', '')), 'A') ||
                            setweight(to_tsvector('english', coalesce(username ->> 'last_name', '')), 'B') ) STORED,
    created_by      TEXT NOT NULL,
    updated_by      TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS users_confirmation
(
    id         BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    user_id    TEXT         NOT NULL,
    expiry     TIMESTAMP(6) NOT NULL,
    key        TEXT         NOT NULL,
    updated_at TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP,
    created_by TEXT         NOT NULL,
    updated_by TEXT         NOT NULL
);

CREATE TABLE IF NOT EXISTS assignment
(
    id         BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    assignment_uuid      UUID DEFAULT gen_random_uuid() UNIQUE,
    assignment_id  TEXT GENERATED ALWAYS AS ( 'ASS-' || assignment_uuid ) STORED ,
    admin_id   BIGINT REFERENCES admin (id),
    updated_at TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP,
    created_by TEXT NOT NULL,
    updated_by TEXT NOT NULL
);



CREATE TABLE IF NOT EXISTS certification
(
    id               BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    certification_uuid           UUID DEFAULT gen_random_uuid() UNIQUE,
    certification_id             TEXT GENERATED ALWAYS AS ( 'CRT-' || certification_uuid ) STORED ,
    consultant_id    BIGINT REFERENCES consultant (id) NOT NULL,
    assignment_id    BIGINT REFERENCES assignment (id) NOT NULL,
    file_name        TEXT                              NOT NULL,
    file_url         TEXT                              NOT NULL,
    certificate_type TEXT                              NOT NULL,
    confirmed        BOOLEAN      DEFAULT TRUE,
    updated_at       TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP,
    created_at       TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP,
    created_by       TEXT                              NOT NULL,
    updated_by       TEXT                              NOT NULL

);

CREATE TABLE IF NOT EXISTS search
(
    id         BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    query      TEXT NOT NULL,
    owner_id   TEXT NOT NULL,
    category   TEXT         DEFAULT 'ALL',
    updated_at TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP,
    created_by TEXT NOT NULL,
    updated_by TEXT NOT NULL
);

CREATE TYPE article_status_enum AS ENUM ('DRAFT','ARCHIVED','PUBLISHED');


CREATE TABLE IF NOT EXISTS article
(
    id             BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    article_uuid           UUID DEFAULT gen_random_uuid() UNIQUE,
    article_id             TEXT GENERATED ALWAYS AS ( 'ART-' || article_uuid ) STORED ,
    title          TEXT                              NOT NULL,
    subtitle       TEXT                              NOT NULL,
    content        TEXT                              NOT NULL,
    like_ids       BIGINT[],
    tags           TEXT[],
    writer_id      BIGINT REFERENCES consultant (id) NOT NULL,
    no_of_reads    BIGINT       DEFAULT 0,
    article_status VARCHAR(10)  DEFAULT 'DRAFT',
    search_vector  TSVECTOR GENERATED ALWAYS AS (
                               setweight(to_tsvector('english', coalesce(title, '')), 'A') ||
                               setweight(to_tsvector('english', coalesce(content, '')), 'B') ||
                               setweight(to_tsvector('english', coalesce(subtitle, '')), 'C')
                       ) STORED,
    updated_at     TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP,
    created_at     TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP,
    published_at   TIMESTAMP(6) DEFAULT NULL,
    created_by     TEXT                              NOT NULL,
    updated_by     TEXT                              NOT NULL

);


CREATE TABLE IF NOT EXISTS consultant_recommendation
(
    id            BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    patient_id    BIGINT REFERENCES patient (id) NOT NULL,
    consultant_id BIGINT REFERENCES consultant (id)             NOT NULL,
    updated_at    TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP,
    created_at    TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP,
    created_by    TEXT                                          NOT NULL,
    updated_by    TEXT                                          NOT NULL
);


CREATE TABLE IF NOT EXISTS article_recommendation
(
    id         BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    patient_id BIGINT REFERENCES patient (id) NOT NULL,
    article_id BIGINT REFERENCES article (id)                NOT NULL,
    updated_at TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP,
    created_by TEXT,
    updated_by TEXT
);



CREATE TABLE IF NOT EXISTS message
(
    id            BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    sender_id     TEXT,
    receiver_id   TEXT,
    content       TEXT,
    timestamp     TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP,
    search_vector tsvector GENERATED ALWAYS AS (
                      setweight(to_tsvector('english', coalesce(content, '')), 'C')
                      ) STORED,
    delivered     BOOLEAN      DEFAULT FALSE,
    updated_at    TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP,
    created_at    TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP,
    created_by    TEXT,
    updated_by    TEXT

);

CREATE TABLE IF NOT EXISTS comment
(
    id                BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    comment_uuid           UUID DEFAULT gen_random_uuid() UNIQUE,
    comment_id             TEXT GENERATED ALWAYS AS ( 'CMT-' || comment_uuid ) STORED ,
    user_id           TEXT NOT NULL,
    article_id        BIGINT REFERENCES article (id),
    parent_comment_id BIGINT REFERENCES comment (id),
    updated_at        TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP,
    created_at        TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP,
    created_by        TEXT,
    updated_by        TEXT
);

-- ALTER TABLE IF EXISTS consultant
--     ADD FOREIGN KEY (certificate_id) REFERENCES certification (id);

CREATE INDEX IF NOT EXISTS idx_search_owner_id ON search (owner_id);
CREATE INDEX IF NOT EXISTS idx_patient_email ON patient (email);
CREATE INDEX IF NOT EXISTS idx_patient_user_id ON patient (user_id);
CREATE INDEX IF NOT EXISTS idx_consultant_email ON consultant (email);
CREATE INDEX IF NOT EXISTS idx_consultant_user_id ON consultant (user_id);
CREATE INDEX IF NOT EXISTS idx_patient_credential ON patient USING GIN (user_credential);
CREATE INDEX IF NOT EXISTS idx_consultant_credential ON consultant USING GIN (user_credential);
CREATE INDEX IF NOT EXISTS article_search_idx ON article USING GIN (search_vector);
CREATE INDEX IF NOT EXISTS article_status_idx on article (article_status);
CREATE INDEX IF NOT EXISTS message_search_idx ON message USING GIN (search_vector);

CREATE TYPE article_search_result AS
(
    id              BIGINT,
    title           TEXT,
    subtitle        TEXT,
    authorFirstName TEXT,
    authorLastName  TEXT,
    publishedAt     TIMESTAMP,
    tags            TEXT[],
    rank            numeric,
    highlighted     TEXT,
    upvotes         BIGINT,
    numberOfComment BIGINT,
    reads           BIGINT,
    total           BIGINT
);

CREATE OR REPLACE FUNCTION search_articles(
    search_query TEXT,
    tag_filter TEXT[] DEFAULT NULL,
    author_filter BIGINT[] DEFAULT NULL,
    min_date TIMESTAMP(6) DEFAULT NULL,
    max_date TIMESTAMP(6) DEFAULT NULL,
    page_size INTEGER DEFAULT 20,
    page_number INTEGER DEFAULT 0
)
    RETURNS setof article_search_result
AS
$$
DECLARE
    tsquery_var tsquery;
    total       BIGINT;
BEGIN
    -- CONVERT SEARCH QUERY TO TS-QUERY, HANDLING MULTIPLE WORDS
    SELECT array_to_string(array_agg(lexeme || ':*'), ' & ')
    FROM unnest(regexp_split_to_array(trim(search_query), '\s+')) lexeme
    INTO search_query;

    tsquery_var := to_tsquery('english', search_query);

    -- GET TOTAL COUNT FOR PAGINATION
    SELECT COUNT(DISTINCT a.id)
    FROM article a
             JOIN consultant c on a.writer_id = c.id
    WHERE a.article_status = 'PUBLISHED'
      AND a.search_vector @@ tsquery_var
      AND (tag_filter IS NULL OR a.tags @> tag_filter)
      AND (author_filter IS NULL OR c.id = any (author_filter))
      AND (min_date IS NULL OR a.published_at >= min_date)
      AND (max_date IS NULL OR a.published_at <= max_date)
    INTO total;

    RETURN QUERY
        WITH ranked_articles AS
                 (SELECT DISTINCT ON (a.id) a.id,
                                            a.title,
                                            a.subtitle,
                                            cp.username ->> 'full_name'                    as firstName,
                                            cp.username ->> 'full_name'                    as lastName,
                                            a.published_at,
                                            a.no_of_reads                                  as reads,
                                            a.tags                                         as tags,
                                            cardinality(a.like_ids)                        as upvotes,
                                            ts_rank(a.search_vector, tsquery_var) *
                                            CASE
                                                WHEN a.published_at > now() - INTERVAL '7 days'
                                                    THEN 1.5 -- boost more recent articles
                                                WHEN a.published_at > now() - INTERVAL '30 days' THEN 1.2
                                                ELSE 1.0
                                                END                                        as rank,
                                            ts_headline('english', a.content, tsquery_var) as highlight,
                                            (SELECT count(c.article_id)
                                             FROM comment c
                                             where c.article_id = a.id)                    as no_of_comments
                  FROM article a
                           LEFT JOIN consultant_profiles cp on cp.id= a.writer_id
                  WHERE a.article_status = 'PUBLISHED'
                    AND a.search_vector @@ tsquery_var
                    AND (tag_filter IS NULL OR a.tags @> tag_filter)
                    AND (author_filter IS NULL OR cp.id = any (author_filter))
                    AND (min_date IS NULL OR a.published_at >= min_date)
                    AND (max_date IS NULL OR a.published_at <= max_date))
        SELECT ra.id             as id,
               ra.title          as title,
               ra.subtitle       as subtitle,
               ra.firstName      as authorFirstName,
               ra.lastName       as authorLastName,
               ra.published_at   as publishedAt,
               ra.tags           as tags,
               ra.rank           as rank,
               ra.highlight      as highlighted,
               ra.upvotes        as upvotes,
               ra.no_of_comments as numberOfComment,
               ra.reads          as reads,
               total
        FROM ranked_articles ra
        ORDER BY ra.rank DESC
        LIMIT page_size OFFSET (page_number * page_size);
END
$$ language plpgsql;


CREATE TYPE message_search_result AS
(
    id                  BIGINT,
    receiver_first_name TEXT,
    receiver_last_name  TEXT,
    content_highlight   TEXT,
    rank                float4,
    total               BIGINT
);

CREATE OR REPLACE FUNCTION search_messages(
    query text,
    page_size INTEGER DEFAULT 20,
    page_number INTEGER DEFAULT 1
) RETURNS SETOF message_search_result
AS
$$
DECLARE
    tsquery_var tsquery;
    total       BIGINT;
BEGIN
    SELECT array_to_string(array_agg(lexeme || ':*'), ' & ')
    FROM unnest(regexp_split_to_array(trim(query), '\s+')) lexeme
    INTO query;

    tsquery_var := to_tsquery('english', query);

    SELECT COUNT(DISTINCT m.id)
    FROM message m
             JOIN consultant c ON c.id = receiver_id
             JOIN patient p ON p.id = receiver_id
            JOIN consultant_profiles cp on c.id = cp.id
            JOIN patient_profiles pp on p.id = pp.id
    WHERE m.search_vector ||
          setweight(to_tsvector('english', coalesce(cp.username ->> 'first_name', pp.username ->> 'first_name', '')),
                    'A') ||
          setweight(to_tsvector('english', coalesce(cp.username ->> 'last_name', pp.username ->> 'last_name', '')),
                    'B') @@ tsquery_var
    INTO total;

    RETURN QUERY
        WITH ranked_messages AS (SELECT DISTINCT on (m2.id) m2.id                                                                  as id,
                                                            coalesce(cp.username ->> 'first_name',
                                                                     pp.username ->> 'first_name',
                                                                     '')                                                           as firstname,
                                                            coalesce(cp.username ->> 'last_name', pp.username ->> 'last_name', '') as lastname,
                                                            ts_rank(m2.search_vector, tsquery_var) *
                                                            CASE
                                                                WHEN m2.created_at > now() - INTERVAL '7 days'
                                                                    THEN 1.5 -- boost more recent articles
                                                                WHEN m2.created_at > now() - INTERVAL '30 days' THEN 1.2
                                                                ELSE 1.0
                                                                END                                                                as rank,
                                                            ts_headline('english', m2.content, tsquery_var)                        as highlight
                                 FROM message m2
                                          JOIN consultant c2 on c2.id = receiver_id
                                          JOIN patient p2 on p2.id = receiver_id
                                          JOIN consultant_profiles cp on c2.id = cp.id
                                          JOIN patient_profiles pp on p2.id = pp.id
                                 WHERE m2.search_vector ||
                                       setweight(to_tsvector('english',
                                                             coalesce(cp.username ->> 'first_name',
                                                                      pp.username ->> 'first_name', '')),
                                                 'A') ||
                                       setweight(to_tsvector('english',
                                                             coalesce(cp.username ->> 'last_name', pp.username ->> 'last_name', '')),
                                                 'B') @@ tsquery_var)
        SELECT rm.id        as id,
               rm.rank      as rank,
               rm.highlight as content_highlight,
               rm.firstname as receiver_first_name,
               rm.lastname  as receiver_last_name,
               total
        FROM ranked_messages rm
        ORDER BY rm.rank DESC
        LIMIT page_size OFFSET ((page_number - 1) * page_size);

END
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION recommendArticlesForPatient(
    patient_id BIGINT,
    page_size INTEGER DEFAULT 20,
    page_number INTEGER DEFAULT 0
) RETURNS SETOF article_search_result AS
$$
DECLARE
    total BIGINT;
BEGIN

    SELECT count(*)
    FROM article a
             LEFT JOIN consultant co ON a.writer_id = co.id
    INTO total;


    RETURN QUERY
        WITH ranked_articles AS (SELECT DISTINCT ON (a.id) a.id,
                                                           a.title                           as title,
                                                           a.subtitle,
                                                           cp.username ->> 'full_name'       as firstName,
                                                           cp.username ->> 'full_name'       as lastName,
                                                           a.published_at,
                                                           a.no_of_reads                     as reads,
                                                           a.tags                            as tags,
                                                           cardinality(a.like_ids) :: BIGINT as upvotes,
                                                           (a.no_of_reads +
                                                            CASE
                                                                WHEN a.published_at > now() - INTERVAL '7 days'
                                                                    THEN 1.5 -- boost more recent articles
                                                                WHEN a.published_at > now() - INTERVAL '30 days'
                                                                    THEN 1.2
                                                                ELSE 1.0
                                                                END) / 1000                  as rank,
                                                           (SELECT count(c.article_id)
                                                            FROM comment c
                                                            where c.article_id = a.id)       as no_of_comments
                                 FROM article a
                                          LEFT JOIN consultant_profiles cp ON a.writer_id = cp.id)
        SELECT ra.id             as id,
               ra.title          as title,
               ra.subtitle       as subtitle,
               ra.firstName      as authorFirstName,
               ra.lastName       as authorLastName,
               ra.published_at   as publishedAt,
               ra.tags           as tags,
               ra.rank           as rank,
               null,
               ra.upvotes        as upvotes,
               ra.no_of_comments as numberOfComment,
               ra.reads          as reads,
               total
        FROM ranked_articles ra
        ORDER BY ra.rank DESC
        LIMIT page_size OFFSET (page_number * page_size);
END;
$$ language plpgsql;

CREATE TYPE user_security_data AS
(
    id              BIGINT,
    email           TEXT,
    user_id         TEXT,
    user_credential jsonb
);

CREATE OR REPLACE FUNCTION find_user_sec_detail_by_email(
    IN input_email TEXT,
    OUT user_security_data user_security_data
) RETURNS user_security_data AS
$$
BEGIN
    SELECT id,
           email,
           user_id,
           user_credential
    INTO user_security_data
    FROM (select patient.id, patient.email, patient.user_id, patient.user_credential
          from patient
          where email = input_email
          UNION
          SELECT consultant.id, consultant.email, consultant.user_id, consultant.user_credential
          from consultant
          where email = input_email
          UNION
          SELECT admin.id, admin.email, admin.user_id, admin.user_credential
          FROM admin
          where email = input_email) as retreived
    LIMIT 1;
END;

$$
    LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION find_user_credentials_by_email(
    IN input_email TEXT,
    OUT user_credential_out jsonb
) RETURNS jsonb AS

$$
BEGIN
    SELECT user_credential
    INTO user_credential_out
    FROM (select patient.user_credential
          from patient
          where email = input_email
          UNION
          SELECT consultant.user_credential
          from consultant
          where email = input_email
          UNION
          SELECT admin.user_credential
          FROM admin
          where email = input_email) as retrieved
    LIMIT 1;

END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION find_user_credentials_by_userId(
    IN input_user_id TEXT,
    OUT user_credential_out jsonb
) RETURNS jsonb AS

$$
BEGIN
    SELECT user_credential
    INTO user_credential_out
    FROM (select patient.user_credential
          from patient
          where user_id = input_user_id
          UNION
          SELECT consultant.user_credential
          from consultant
          where user_id = input_user_id
          UNION
          SELECT admin.user_credential
          FROM admin
          where user_id = input_user_id) as retreived
    LIMIT 1;

END;
$$ LANGUAGE plpgsql;

END;






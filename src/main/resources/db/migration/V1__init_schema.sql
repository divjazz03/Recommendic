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
    search,
    message,
    consultation CASCADE;
DROP TYPE IF EXISTS article_search_result, message_search_result, user_security_data CASCADE;

DROP INDEX IF EXISTS
    article_search_idx,
    idx_consultant_email,
    idx_consultant_user_id,
    idx_search_owner_id,
    patient_schema.idx_patient_email,
    patient_schema.idx_patient_user_id,
    consultation_search_idx,
    message_search_idx CASCADE;
DROP SCHEMA IF EXISTS patient_schema, consultant_schema, admin_schema CASCADE ;

CREATE EXTENSION IF NOT EXISTS pg_trgm;
CREATE EXTENSION IF NOT EXISTS unaccent;
CREATE SCHEMA patient_schema;
CREATE SCHEMA consultant_schema;
CREATE SCHEMA admin_schema;
/*                                              USER TABLE                                                             */
CREATE TABLE IF NOT EXISTS admin
(
    id                  BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    user_id             CHARACTER VARYING(54) UNIQUE NOT NULL,
    username            jsonb,
    email               CHARACTER VARYING(54) UNIQUE NOT NULL,
    phone_number        CHARACTER VARYING(54)                 DEFAULT NULL,
    bio                 TEXT                                  DEFAULT NULL,
    profile_picture     jsonb,
    address             jsonb,
    user_type           CHARACTER VARYING(54)        NOT NULL,
    user_stage          CHARACTER VARYING(54)        NOT NULL,
    enabled             BOOLEAN                      NOT NULL DEFAULT FALSE,
    account_non_expired BOOLEAN                      NOT NULL DEFAULT TRUE,
    account_non_locked  BOOLEAN                      NOT NULL DEFAULT TRUE,
    gender              CHARACTER VARYING(54)        NOT NULL,
    role                CHARACTER VARYING(54)        NOT NULL,
    last_login          TIMESTAMP(6) WITH TIME ZONE           DEFAULT NULL,
    updated_at          TIMESTAMP(6) WITH TIME ZONE           DEFAULT CURRENT_TIMESTAMP,
    created_at          TIMESTAMP(6) WITH TIME ZONE           DEFAULT CURRENT_TIMESTAMP,
    created_by          CHARACTER VARYING(54),
    updated_by          CHARACTER VARYING(54),
    /*Credential embed*/
    user_credential     jsonb

);
/*                                             PATIENT                                                          */
CREATE TABLE IF NOT EXISTS patient_schema.patient
(
    id                  BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    user_id             CHARACTER VARYING(54) UNIQUE NOT NULL,
    username            jsonb,
    email               CHARACTER VARYING(54) UNIQUE NOT NULL,
    phone_number        CHARACTER VARYING(54)                 DEFAULT NULL,
    bio                 TEXT                                  DEFAULT NULL,
    profile_picture     jsonb,
    address             jsonb,
    user_type           CHARACTER VARYING(54)        NOT NULL,
    user_stage          CHARACTER VARYING(54)        NOT NULL,
    enabled             BOOLEAN                      NOT NULL DEFAULT FALSE,
    account_non_expired BOOLEAN                      NOT NULL DEFAULT TRUE,
    account_non_locked  BOOLEAN                      NOT NULL DEFAULT TRUE,
    gender              CHARACTER VARYING(54)        NOT NULL,
    role                CHARACTER VARYING(54)        NOT NULL,
    last_login          TIMESTAMP(6) WITH TIME ZONE           DEFAULT NULL,
    updated_at          TIMESTAMP(6) WITH TIME ZONE           DEFAULT CURRENT_TIMESTAMP,
    created_at          TIMESTAMP(6) WITH TIME ZONE           DEFAULT CURRENT_TIMESTAMP,
    created_by          CHARACTER VARYING(54),
    updated_by          CHARACTER VARYING(54),
    medical_categories  TEXT[],
    recommendation_id   BIGINT,
    user_credential     jsonb

);
CREATE TABLE IF NOT EXISTS consultant
(
    id                  BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    user_id             CHARACTER VARYING(54) UNIQUE NOT NULL,
    username            jsonb,
    email               CHARACTER VARYING(54) UNIQUE NOT NULL,
    phone_number        CHARACTER VARYING(54)                 DEFAULT NULL,
    bio                 TEXT                                  DEFAULT NULL,
    profile_picture     jsonb,
    address             jsonb,
    user_type           CHARACTER VARYING(54)        NOT NULL,
    user_stage          CHARACTER VARYING(54)        NOT NULL,
    enabled             BOOLEAN                      NOT NULL DEFAULT FALSE,
    account_non_expired BOOLEAN                      NOT NULL DEFAULT TRUE,
    account_non_locked  BOOLEAN                      NOT NULL DEFAULT TRUE,
    gender              CHARACTER VARYING(54)        NOT NULL,
    role                CHARACTER VARYING(54)        NOT NULL,
    last_login          TIMESTAMP(6) WITH TIME ZONE           DEFAULT NULL,
    updated_at          TIMESTAMP(6) WITH TIME ZONE           DEFAULT CURRENT_TIMESTAMP,
    created_at          TIMESTAMP(6) WITH TIME ZONE           DEFAULT CURRENT_TIMESTAMP,
    created_by          CHARACTER VARYING(54),
    updated_by          CHARACTER VARYING(54),
    specialization      CHARACTER VARYING(54),
    certified           BOOLEAN                               DEFAULT FALSE,
    certificate_id      BIGINT,
    user_credential     jsonb

);

CREATE TABLE IF NOT EXISTS users_confirmation
(
    id         BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    user_id    CHARACTER VARYING(54)              NOT NULL,
    expiry     TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    key        CHARACTER VARYING(100)      NOT NULL,
    updated_at TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by CHARACTER VARYING(54)       NOT NULL,
    updated_by CHARACTER VARYING(54)       NOT NULL
);

CREATE TABLE IF NOT EXISTS assignment
(
    id         BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    admin_id   BIGINT REFERENCES admin (id),
    updated_at TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by CHARACTER VARYING(54) NOT NULL,
    updated_by CHARACTER VARYING(54) NOT NULL
);



CREATE TABLE IF NOT EXISTS certification
(
    id               BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    consultant_id    BIGINT REFERENCES consultant (id) NOT NULL,
    assignment_id    BIGINT REFERENCES assignment (id) NOT NULL,
    file_name        CHARACTER VARYING(255)            NOT NULL,
    file_url         CHARACTER VARYING(255)            NOT NULL,
    certificate_type CHARACTER VARYING(30)             NOT NULL,
    confirmed        BOOLEAN                     DEFAULT TRUE,
    updated_at       TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_at       TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by       CHARACTER VARYING(54)             NOT NULL,
    updated_by       CHARACTER VARYING(54)             NOT NULL

);

CREATE TABLE IF NOT EXISTS search
(
    id         BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    query      CHARACTER VARYING(30) NOT NULL,
    owner_id   CHARACTER VARYING(54) NOT NULL,
    category   CHARACTER VARYING(30)       DEFAULT 'ALL',
    updated_at TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by CHARACTER VARYING(54) NOT NULL,
    updated_by CHARACTER VARYING(54) NOT NULL
);


CREATE TABLE IF NOT EXISTS article
(
    id             BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    title          CHARACTER VARYING(54)             NOT NULL,
    subtitle       CHARACTER VARYING(54)             NOT NULL,
    content        TEXT                              NOT NULL,
    like_ids       BIGINT[],
    tags           CHARACTER VARYING(50)[],
    writer_id      BIGINT REFERENCES consultant (id) NOT NULL,
    no_of_reads    BIGINT                      DEFAULT 0,
    article_status CHARACTER VARYING(10)             NOT NULL,
    search_vector  TSVECTOR GENERATED ALWAYS AS (
                               setweight(to_tsvector('english', coalesce(title, '')), 'A') ||
                               setweight(to_tsvector('english', coalesce(content, '')), 'B') ||
                               setweight(to_tsvector('english', coalesce(subtitle, '')), 'C')
                       ) STORED,
    updated_at     TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_at     TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    published_at   TIMESTAMP(6) WITH TIME ZONE DEFAULT NULL,
    created_by     CHARACTER VARYING(54)             NOT NULL,
    updated_by     CHARACTER VARYING(54)             NOT NULL

);


CREATE TABLE IF NOT EXISTS consultant_recommendation
(
    id            BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    patient_id    BIGINT REFERENCES patient_schema.patient (id) NOT NULL,
    consultant_id BIGINT REFERENCES consultant (id)             NOT NULL,
    updated_at    TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_at    TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by    CHARACTER VARYING(54)                         NOT NULL,
    updated_by    CHARACTER VARYING(54)                         NOT NULL
);


CREATE TABLE IF NOT EXISTS article_recommendation
(
    id         BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    patient_id BIGINT REFERENCES patient_schema.patient (id) NOT NULL,
    article_id BIGINT REFERENCES article (id)                NOT NULL,
    updated_at TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by CHARACTER VARYING(54),
    updated_by CHARACTER VARYING(54)
);

CREATE TABLE IF NOT EXISTS consultation
(
    id                BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    diagnosis         TEXT,
    consultation_time TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    consultation_id   CHARACTER VARYING(54) UNIQUE,
    patient_id        BIGINT REFERENCES patient_schema.patient (id) NOT NULL,
    consultant_id     BIGINT REFERENCES consultant (id)             NOT NULL,
    accepted          BOOLEAN                     DEFAULT FALSE,
    status            CHARACTER VARYING(10)       DEFAULT 'NOT_STARTED',
    search_vector     TSVECTOR GENERATED ALWAYS AS (
                              setweight(to_tsvector('english', coalesce(diagnosis, '')), 'A') ||
                              setweight(to_tsvector('english', coalesce(status, '')), 'B')
                          ) STORED,
    updated_at        TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_at        TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by        CHARACTER VARYING(54),
    updated_by        CHARACTER VARYING(54)
);

CREATE TABLE IF NOT EXISTS message
(
    id              BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    sender_id       CHARACTER VARYING(54),
    receiver_id     CHARACTER VARYING(54),
    consultation_id CHARACTER VARYING(54) REFERENCES consultation (consultation_id),
    content         TEXT,
    timestamp       TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    search_vector   tsvector GENERATED ALWAYS AS (
                        setweight(to_tsvector('english', coalesce(content, '')), 'C')
                        ) STORED,
    delivered       BOOLEAN                     DEFAULT FALSE,
    updated_at      TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_at      TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by      CHARACTER VARYING(54),
    updated_by      CHARACTER VARYING(54)

);

CREATE TABLE IF NOT EXISTS comment
(
    id                BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    user_id           CHARACTER VARYING(54) NOT NULL,
    article_id        BIGINT REFERENCES article (id),
    parent_comment_id BIGINT REFERENCES comment (id),
    updated_at        TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_at        TIMESTAMP(6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by        CHARACTER VARYING(54),
    updated_by        CHARACTER VARYING(54)
);

ALTER TABLE IF EXISTS consultant
    ADD FOREIGN KEY (certificate_id) REFERENCES certification (id);

CREATE INDEX IF NOT EXISTS idx_search_owner_id ON search (owner_id);
CREATE INDEX IF NOT EXISTS idx_patient_email ON patient_schema.patient (email);
CREATE INDEX IF NOT EXISTS idx_patient_user_id ON patient_schema.patient (user_id);
CREATE INDEX IF NOT EXISTS idx_consultant_email ON consultant (email);
CREATE INDEX IF NOT EXISTS idx_consultant_user_id ON consultant (user_id);
CREATE INDEX IF NOT EXISTS idx_patient_credential ON patient_schema.patient USING GIN (user_credential);
CREATE INDEX IF NOT EXISTS idx_consultant_credential ON consultant USING GIN (user_credential);
CREATE INDEX IF NOT EXISTS article_search_idx ON article USING GIN (search_vector);
CREATE INDEX IF NOT EXISTS consultation_search_idx ON consultation USING GIN (search_vector);
CREATE INDEX IF NOT EXISTS article_status_idx on article (article_status);
CREATE INDEX IF NOT EXISTS message_search_idx ON message USING GIN (search_vector);

CREATE TYPE article_search_result AS
(
    id              BIGINT,
    title           CHARACTER VARYING(54),
    subtitle        CHARACTER VARYING(54),
    authorFirstName TEXT,
    authorLastName  TEXT,
    published_at    TIMESTAMP WITH TIME ZONE,
    rank            float4,
    highlight       TEXT,
    total           BIGINT
);

CREATE OR REPLACE FUNCTION search_articles(
    search_query TEXT,
    tag_filter CHARACTER VARYING(50)[] DEFAULT NULL,
    author_filter BIGINT[] DEFAULT NULL,
    min_date TIMESTAMP(6) WITH TIME ZONE DEFAULT NULL,
    max_date TIMESTAMP(6) WITH TIME ZONE DEFAULT NULL,
    page_size INTEGER DEFAULT 20,
    page_number INTEGER DEFAULT 1
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
                                            co.username ->> 'full_name'                    as firstName,
                                            co.username ->> 'full_name'                    as lastName,
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
                           LEFT JOIN consultant co on co.id = a.writer_id
                  WHERE a.article_status = 'PUBLISHED'
                    AND a.search_vector @@ tsquery_var
                    AND (tag_filter IS NULL OR a.tags @> tag_filter)
                    AND (author_filter IS NULL OR co.id = any (author_filter))
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
        LIMIT page_size OFFSET ((page_number) - 1 * page_size);
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
    page_number INTEGER DEFAULT 0
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
             JOIN patient_schema.patient p ON p.id = receiver_id
    WHERE m.search_vector ||
          setweight(to_tsvector('english', coalesce(c.username ->> 'first_name', p.username ->> 'first_name', '')),
                    'A') ||
          setweight(to_tsvector('english', coalesce(c.username ->> 'last_name', p.username ->> 'last_name', '')),
                    'B') @@ tsquery_var
    INTO total;

    RETURN QUERY
        WITH ranked_messages AS (SELECT DISTINCT on (m2.id) m2.id                                                                  as id,
                                                            coalesce(c2.username ->> 'first_name',
                                                                     p2.username ->> 'first_name',
                                                                     '')                                                           as firstname,
                                                            coalesce(c2.username ->> 'last_name', p2.username ->> 'last_name', '') as lastname,
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
                                          JOIN patient_schema.patient p2 on p2.id = receiver_id
                                 WHERE m2.search_vector @@ tsquery_var)
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
    page_size INTEGER,
    page_number INTEGER
) RETURNS SETOF article_search_result AS
$$
DECLARE
    query       text;
    tsquery_var tsquery;
BEGIN

    SELECT array_to_string(array_agg(DISTINCT tag || ':*'), ' & ')
    FROM patient_schema.patient p,
         (SELECT unnest(a.tags) as tag
          FROM article a) as rtag
    WHERE p.id = patient_id
    INTO query;

    tsquery_var := to_tsquery('english', query);

    RETURN QUERY
        WITH ranked_articles AS (SELECT DISTINCT ON (a.id) a.id,
                                                           a.title                     as title,
                                                           a.subtitle,
                                                           co.username ->> 'full_name' as firstName,
                                                           co.username ->> 'full_name' as lastName,
                                                           a.published_at,
                                                           a.no_of_reads               as reads,
                                                           a.tags                      as tags,
                                                           cardinality(a.like_ids)     as upvotes,
                                                           ts_rank(to_tsvector('english', array_to_string(tags, ' ')),
                                                                   tsquery_var) *
                                                           CASE
                                                               WHEN a.published_at > now() - INTERVAL '7 days'
                                                                   THEN 1.5 -- boost more recent articles
                                                               WHEN a.published_at > now() - INTERVAL '30 days' THEN 1.2
                                                               ELSE 1.0
                                                               END                     as rank,
                                                           (SELECT count(c.article_id)
                                                            FROM comment c
                                                            where c.article_id = a.id) as no_of_comments
                                 FROM article a
                                          JOIN consultant co ON a.writer_id = co.id)
        SELECT ra.id             as id,
               ra.title          as title,
               ra.subtitle       as subtitle,
               ra.firstName      as authorFirstName,
               ra.lastName       as authorLastName,
               ra.published_at   as publishedAt,
               ra.tags           as tags,
               ra.rank           as rank,
               ra.upvotes        as upvotes,
               ra.no_of_comments as numberOfComment,
               ra.reads          as reads,
               total
        FROM ranked_articles ra
        ORDER BY ra.rank DESC
        LIMIT page_size OFFSET ((page_number) - 1 * page_size);
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
          from patient_schema.patient
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
          from patient_schema.patient
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
          from patient_schema.patient
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






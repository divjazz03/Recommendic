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
    FROM (select patients.id, patients.email, patients.user_id, patients.user_credential
          from patients
          where email = input_email
          UNION
          SELECT consultants.id, consultants.email, consultants.user_id, consultants.user_credential
          from consultants
          where email = input_email
          UNION
          SELECT admins.id, admins.email, admins.user_id, admins.user_credential
          FROM admins
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
    FROM (select patients.user_credential
          from patients
          where email = input_email
          UNION
          SELECT consultants.user_credential
          from consultants
          where email = input_email
          UNION
          SELECT admins.user_credential
          FROM admins
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
    FROM (select patients.user_credential
          from patients
          where user_id = input_user_id
          UNION
          SELECT consultants.user_credential
          from consultants
          where user_id = input_user_id
          UNION
          SELECT admins.user_credential
          FROM admins
          where user_id = input_user_id) as retreived
    LIMIT 1;

END;
$$ LANGUAGE plpgsql;

END;

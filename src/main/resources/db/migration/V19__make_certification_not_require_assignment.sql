
ALTER TABLE certification
    DROP COLUMN assignment_id;
ALTER TABLE certification
    ADD COLUMN assignment_id BIGINT REFERENCES assignment (id);
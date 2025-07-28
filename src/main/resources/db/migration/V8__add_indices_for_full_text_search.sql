DROP INDEX IF EXISTS idx_consultant_profile_search_vector, idx_consultant_search_vector ;

ALTER TABLE consultant DROP COLUMN search_vector;
ALTER TABLE consultant ADD COLUMN search_vector tsvector GENERATED ALWAYS AS (
    setweight(to_tsvector('english', coalesce(email, '')), 'A') ||
    setweight(to_tsvector('english', coalesce(specialization, '')), 'C')
    ) STORED;

CREATE INDEX IF NOT EXISTS idx_consultant_search_vector ON consultant USING gin(search_vector);
CREATE INDEX IF NOT EXISTS idx_consultant_profile_search_vector ON consultant_profiles USING gin(search_vector);

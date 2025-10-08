DROP INDEX IF EXISTS idx_consultant_profile_search_vector ;

CREATE INDEX IF NOT EXISTS idx_consultant_profile_search_vector ON consultant_profiles USING gin(search_vector);

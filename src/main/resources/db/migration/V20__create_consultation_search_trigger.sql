CREATE OR REPLACE FUNCTION update_consultation_search_vector()
    RETURNS TRIGGER AS
$$
BEGIN
    NEW.search_vector := setweight(to_tsvector('english', coalesce(NEW.channel::text, 'CHAT')), 'B') ||
                         setweight(to_tsvector('english', coalesce(NEW.status::text, '')), 'A');
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_update_search_vector
    BEFORE INSERT OR UPDATE
    ON consultations
    FOR EACH ROW
EXECUTE FUNCTION update_consultation_search_vector();

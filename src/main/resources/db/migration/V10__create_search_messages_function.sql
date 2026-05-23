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
    FROM messages m
             JOIN consultant c ON c.id = m.receiver_id
             JOIN patient p ON p.id = m.receiver_id
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

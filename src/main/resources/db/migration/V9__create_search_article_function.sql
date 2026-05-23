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
    FROM articles a
             JOIN consultants c on a.writer_id = c.id
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
                                             FROM comments c
                                             where c.article_id = a.id)                    as no_of_comments
                  FROM articles a
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
        OFFSET ((page_number + 1) * page_size)
        LIMIT page_size
    ;
END
$$ language plpgsql;

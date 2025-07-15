CREATE OR REPLACE FUNCTION retrieveTopArticles(
    page_size INTEGER DEFAULT 20,
    page_number INTEGER DEFAULT 0
) RETURNS SETOF article_search_result AS
$$
    DECLARE
        total INTEGER;
    BEGIN

        SELECT count(*)
            FROM article a
            left join consultant co ON a.writer_id = co.id
        INTO total;

    RETURN QUERY
        WITH ranked_articles as (SELECT DISTINCT a.id                       as id,
                                                 a.title                     as title,
                                                 a.subtitle,
                                                 co.username ->> 'full_name' as firstName,
                                                 co.username ->> 'full_name' as lastName,
                                                 a.published_at,
                                                 a.no_of_reads               as reads,
                                                 a.tags                      as tags,
                                                 cardinality(a.like_ids)     as upvotes,
                                                 (SELECT count(c.article_id)
                                                  FROM comment c
                                                  where c.article_id = a.id) as no_of_comments,
                                                 (CASE
                                                     WHEN a.published_at > now() - INTERVAL '7 days'
                                                         THEN 1.5 -- boost more recent articles
                                                     WHEN a.published_at > now() - INTERVAL '30 days' THEN 1.2
                                                     ELSE 1.0
                                                 END )
                                                     * (a.no_of_reads + 0.1)
                                                     * (cardinality(a.like_ids) + 0.1) as rank
                                 FROM article a
                                          LEFT JOIN consultant_profiles co ON a.writer_id = co.id
                                )
        SELECT
               ra.id             as id,
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
        LIMIT page_size;

    END
$$ LANGUAGE plpgsql;
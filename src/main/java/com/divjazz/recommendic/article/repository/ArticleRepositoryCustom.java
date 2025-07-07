package com.divjazz.recommendic.article.repository;

import com.divjazz.recommendic.article.dto.ArticleSearchDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

@RequiredArgsConstructor
@Component
public class ArticleRepositoryCustom {
    private final JdbcTemplate jdbcTemplate;
    public Set<ArticleSearchDTO> recommendArticleForPatient(int pageNumber, int pageSize) {
        String sql = "SELECT * FROM recommendarticlesforpatient(?,?,?)";
        var result = jdbcTemplate.query(sql,
                (rs, rsIndex) ->
                        new ArticleSearchDTO(
                                rs.getLong("id"),
                                rs.getString("title"),
                                rs.getString("subtitle"),
                                rs.getString("authorFirstName"),
                                rs.getString("authorLastName"),
                                rs.getString("publishedAt"),
                                (String[]) rs.getArray("tags").getArray(),
                                rs.getFloat("rank"),
                                null,
                                rs.getLong("upvotes"),
                                rs.getLong("numberOfComment"),
                                rs.getLong("reads"),
                                rs.getLong("total")
                        )
                ,null,
                pageSize,
                pageNumber);
        var sortedSet = new TreeSet<>(Comparator.comparing(ArticleSearchDTO::rank));
        sortedSet.addAll(result);
        return sortedSet;
    }
}

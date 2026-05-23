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

}

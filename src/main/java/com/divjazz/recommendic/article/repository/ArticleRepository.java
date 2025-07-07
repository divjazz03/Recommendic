package com.divjazz.recommendic.article.repository;

import com.divjazz.recommendic.article.dto.ArticleDTO;
import com.divjazz.recommendic.article.dto.ArticleSearchDTO;
import com.divjazz.recommendic.article.model.Article;
import com.divjazz.recommendic.user.model.Consultant;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.stream.Stream;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {

    @Query(value = """ 
            select from search_articles(:query, null, null, null, null, :size, :page)
            """, nativeQuery = true)
    Set<ArticleSearchDTO> queryArticle(@Param("query") String query, @Param("size") int size, @Param("page") int page);

    Page<Article> queryArticleByConsultant(Consultant consultant, Pageable pageable);
    Stream<Article> findArticleByConsultant(Consultant consultant);
    @Query(value = """
    select from retrievetoparticles(:size, :page)
    """, nativeQuery = true)
    Set<ArticleSearchDTO> queryTopArticle(@Param("size") int size, @Param("page") int page);

    @Query(value = """
        select from recommendArticlesForPatient(:patient_id, :size, :page)
    """, nativeQuery = true)
    Set<ArticleSearchDTO> recommendArticleToPatient(@Param("patient_id") Long patientId,  @Param("size") int size, @Param("page") int page);

}

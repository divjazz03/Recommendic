package com.divjazz.recommendic.article.repository;

import com.divjazz.recommendic.article.model.Article;
import com.divjazz.recommendic.user.enums.MedicalCategory;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {

    @Query(value = """ 
    select a from article a where to_tsvector('english', title) or to_tsvector('english', content) @@ to_tsquery('english', :query)
    """, nativeQuery = true)
    Page<Article> queryArticle(@Param("query") String query, Pageable pageable);

    @NotNull
    Page<Article> findAll(@NotNull Pageable pageable);

    @Query(value = """
            select a from article a inner join consultant c on c.id = a.consultant_id where c.specialization = :medicalCategory
            """, nativeQuery = true)
    Page<Article> findAllByMedicalCategoryOfInterest(@Param("medicalCategory") String medicalCategory, Pageable pageable);

}

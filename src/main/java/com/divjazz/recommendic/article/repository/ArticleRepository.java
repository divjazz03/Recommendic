package com.divjazz.recommendic.article.repository;

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

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {

    @Query(value = """ 
            select * from search_articles(:query, null, null, null, null, :size, :page)
            """, nativeQuery = true)
    Set<ArticleSearchDTO> queryArticle(@Param("query") String query, @Param("size") int size, @Param("page") int page);

    @NotNull
    Page<Article> findAll(@NotNull Pageable pageable);

    @Query(value = """
            select a from article a left join users c on c.id = a.writer_id where c.specialization = :medicalCategory
            """, nativeQuery = true)
    Page<Article> findAllByMedicalCategoryOfInterest(@Param("medicalCategory") String medicalCategory, Pageable pageable);

    Page<Article> queryArticleByConsultant(Consultant consultant, Pageable pageable);

    @Query(value = """
        select * from recommendArticlesForPatient(:patient_id, :size, :page)
    """, nativeQuery = true)
    Set<ArticleSearchDTO> recommendArticleToPatient(@Param("patient_id") Long patientId,  @Param("size") int size, @Param("page") int page);

}

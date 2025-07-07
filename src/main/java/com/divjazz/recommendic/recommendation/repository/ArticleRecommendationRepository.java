package com.divjazz.recommendic.recommendation.repository;

import com.divjazz.recommendic.recommendation.model.ArticleRecommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleRecommendationRepository extends JpaRepository<ArticleRecommendation, Long> {

}

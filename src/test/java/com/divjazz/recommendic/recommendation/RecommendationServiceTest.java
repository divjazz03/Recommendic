package com.divjazz.recommendic.recommendation;

import com.divjazz.recommendic.recommendation.repository.ArticleRecommendationRepository;
import com.divjazz.recommendic.recommendation.repository.ConsultantRecommendationRepository;
import com.divjazz.recommendic.recommendation.service.RecommendationService;
import com.divjazz.recommendic.search.service.SearchService;
import com.divjazz.recommendic.user.service.ConsultantService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class RecommendationServiceTest {

    @Mock
    private ConsultantRecommendationRepository consultantRecommendationRepository;
    @Mock
    private ArticleRecommendationRepository articleRecommendationRepository;
    @Mock
    private ConsultantService consultantService;
    @Mock
    private SearchService searchService;
    @InjectMocks
    private RecommendationService recommendationService;

    @Test
    void givenPatientShouldCreateArticleRecommendations() {

    }




}

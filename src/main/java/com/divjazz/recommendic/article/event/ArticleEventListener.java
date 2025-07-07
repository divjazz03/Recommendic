package com.divjazz.recommendic.article.event;

import com.divjazz.recommendic.recommendation.service.RecommendationService;
import com.divjazz.recommendic.user.model.Patient;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ArticleEventListener {

    private final RecommendationService recommendationService;
    @EventListener
    public void onEvent(ArticleEvent articleEvent) {
        switch (articleEvent.getEventType()) {
            case ARTICLE_PATIENT_REQUESTED ->  recommendationService
                    .createArticleRecommendationForPatient((Patient) articleEvent.getUser(), articleEvent.getArticle());
        }
    }
}

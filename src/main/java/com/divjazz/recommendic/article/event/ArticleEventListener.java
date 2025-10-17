package com.divjazz.recommendic.article.event;

import com.divjazz.recommendic.recommendation.service.RecommendationService;
import com.divjazz.recommendic.user.model.Patient;
import com.divjazz.recommendic.user.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ArticleEventListener {

    private final RecommendationService recommendationService;
    private final PatientRepository patientRepository;
    @EventListener
    public void onEvent(ArticleEvent articleEvent) {
        switch (articleEvent.getEventType()) {
            case ARTICLE_PATIENT_REQUESTED -> {
                Patient patient = patientRepository.getReferenceById(articleEvent.getUser().id());
                recommendationService
                    .createArticleRecommendationForPatient(patient, articleEvent.getArticle());
            }
        }
    }
}

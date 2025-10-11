package com.divjazz.recommendic.recommendation.service;

import com.divjazz.recommendic.article.model.Article;
import com.divjazz.recommendic.article.service.ArticleService;
import com.divjazz.recommendic.recommendation.model.ArticleRecommendation;
import com.divjazz.recommendic.recommendation.model.ConsultantRecommendation;
import com.divjazz.recommendic.recommendation.repository.ArticleRecommendationRepository;
import com.divjazz.recommendic.recommendation.repository.ConsultantRecommendationRepository;
import com.divjazz.recommendic.search.model.Search;
import com.divjazz.recommendic.user.domain.MedicalCategory;
import com.divjazz.recommendic.user.model.Consultant;
import com.divjazz.recommendic.user.model.Patient;
import com.divjazz.recommendic.user.service.ConsultantService;
import com.divjazz.recommendic.user.service.MedicalCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final ConsultantRecommendationRepository consultantRecommendationRepository;
    private final ArticleRecommendationRepository articleRecommendationRepository;
    private final ConsultantService consultantService;
    private final ArticleService articleService;
    private final MedicalCategoryService medicalCategoryService;


    public Set<ConsultantRecommendation> retrieveRecommendationByPatient(Patient patient) {
        return consultantRecommendationRepository.findByPatient(patient).orElse(Set.of());

    }

    @Async("recommendicTaskExecutor")
    public void createArticleRecommendationForPatient(Patient patient, Article article) {
        articleRecommendationRepository.save(new ArticleRecommendation(patient, article));
    }
    @Async("recommendicTaskExecutor")
    public void createConsultantRecommendationForPatient(Patient patient) {
        Set<Consultant> consultants = new HashSet<>(consultantService.getAllConsultants());
        consultants.forEach(consultant -> consultantRecommendationRepository.save(new ConsultantRecommendation(consultant, patient)));
    }
    @Async("recommendicTaskExecutor")
    public void createConsultantRecommendationForPatient(Patient patient, Consultant consultant){
        consultantRecommendationRepository.save(new ConsultantRecommendation(consultant, patient));
    }

    private Set<Consultant> retrieveConsultantsBasedOnMedicalCategories(Set<MedicalCategory> medicalCategories) {
        return medicalCategories.stream()
                .map(medicalCategory -> medicalCategoryService.getMedicalCategoryByName(medicalCategory.name()))
                .flatMap(medicalCategory -> consultantService
                        .getConsultantsByCategory(medicalCategory)
                        .stream())
                .collect(Collectors.toSet());
    }


}

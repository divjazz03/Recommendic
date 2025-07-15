package com.divjazz.recommendic.recommendation.service;

import com.divjazz.recommendic.article.model.Article;
import com.divjazz.recommendic.article.service.ArticleService;
import com.divjazz.recommendic.recommendation.model.ArticleRecommendation;
import com.divjazz.recommendic.recommendation.model.ConsultantRecommendation;
import com.divjazz.recommendic.recommendation.repository.ArticleRecommendationRepository;
import com.divjazz.recommendic.recommendation.repository.ConsultantRecommendationRepository;
import com.divjazz.recommendic.search.model.Search;
import com.divjazz.recommendic.user.domain.MedicalCategory;
import com.divjazz.recommendic.user.enums.MedicalCategoryEnum;
import com.divjazz.recommendic.user.model.Consultant;
import com.divjazz.recommendic.user.model.Patient;
import com.divjazz.recommendic.user.service.ConsultantService;
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



    public Set<ConsultantRecommendation> retrieveRecommendationByPatient(Patient patient) {
        return consultantRecommendationRepository.findByPatient(patient).orElse(Set.of());

    }
    @Async("recommendicTaskExecutor")
    public void createArticleRecommendationsForPatient(Patient patient) {
        var medicalCategories = patient.getMedicalCategories().stream()
                .map(MedicalCategoryEnum::fromValue)
                .map(medicalCategoryEnum ->
                        new MedicalCategory(medicalCategoryEnum.getValue(), medicalCategoryEnum.getDescription()))
                .collect(Collectors.toSet());

        var articlesByConsultantSpecialty = retrieveConsultantsBasedOnMedicalCategories(medicalCategories)
                .stream()
                .flatMap(articleService::getArticleByConsultant)
                .map(article -> new ArticleRecommendation(patient, article))
                .collect(Collectors.toSet());

        articleRecommendationRepository.saveAll(articlesByConsultantSpecialty);

    }
    @Async("recommendicTaskExecutor")
    public void createArticleRecommendationForPatient(Patient patient, Article article) {
        articleRecommendationRepository.save(new ArticleRecommendation(patient, article));
    }
    @Async("recommendicTaskExecutor")
    public void createConsultantRecommendationForPatient(Patient patient) {
        Set<Consultant> consultants = new HashSet<>();

        patient.getMedicalCategories()
                .forEach(category -> consultants.addAll(consultantService.getConsultantsByCategory(MedicalCategoryEnum.fromValue(category))));
        consultants.forEach(consultant -> consultantRecommendationRepository.save(new ConsultantRecommendation(consultant, patient)));
    }
    @Async("recommendicTaskExecutor")
    public void createConsultantRecommendationForPatient(Patient patient, Consultant consultant){
        consultantRecommendationRepository.save(new ConsultantRecommendation(consultant, patient));
    }

    private Set<Consultant> retrieveConsultantsBasedOnMedicalCategories(Set<MedicalCategory> medicalCategories) {
        return medicalCategories.stream()
                .map(medicalCategory -> MedicalCategoryEnum.fromValue(medicalCategory.name()))
                .flatMap(medicalCategory -> consultantService
                        .getConsultantsByCategory(medicalCategory)
                        .stream())
                .collect(Collectors.toSet());
    }

    private Set<Consultant> retrieveConsultantsBasedOnPatientSearchHistory(Set<Search> searches) {
        Set<MedicalCategory> medicalCategories = new HashSet<>(30);
        for (Search search : searches) {
            for (MedicalCategoryEnum medicalCategoryEnum : MedicalCategoryEnum.values()) {
                if (search.getQuery().matches("[" + medicalCategoryEnum.toString().toLowerCase() + "]")) {
                    medicalCategories.add(new MedicalCategory(medicalCategoryEnum.getValue(),
                            medicalCategoryEnum.getDescription()));
                }
            }
        }
        return retrieveConsultantsBasedOnMedicalCategories(medicalCategories);
    }


}

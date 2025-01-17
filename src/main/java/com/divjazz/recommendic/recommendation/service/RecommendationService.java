package com.divjazz.recommendic.recommendation.service;

import com.divjazz.recommendic.recommendation.model.ConsultantRecommendation;
import com.divjazz.recommendic.recommendation.repository.RecommendationRepository;
import com.divjazz.recommendic.search.model.Search;
import com.divjazz.recommendic.search.service.SearchService;
import com.divjazz.recommendic.user.dto.ConsultantInfoResponse;
import com.divjazz.recommendic.user.dto.PatientInfoResponse;
import com.divjazz.recommendic.user.enums.MedicalCategory;
import com.divjazz.recommendic.user.model.Consultant;
import com.divjazz.recommendic.user.model.Patient;
import com.divjazz.recommendic.user.service.ConsultantService;
import com.divjazz.recommendic.user.service.PatientService;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RecommendationService {

    private final RecommendationRepository recommendationRepository;
    private final ConsultantService consultantService;
    private final PatientService patientService;
    private final SearchService searchService;

    public RecommendationService(RecommendationRepository recommendationRepository, ConsultantService consultantService, PatientService patientService, SearchService searchService) {
        this.recommendationRepository = recommendationRepository;
        this.consultantService = consultantService;
        this.patientService = patientService;
        this.searchService = searchService;
    }

    public Set<ConsultantRecommendation> retrieveRecommendationByPatient(Patient patient) {
        createRecommendationForPatient(patient);

        return recommendationRepository.findByPatient(patient).orElse(Set.of());

    }

    private ConsultantInfoResponse toConsultantInfoResponse(Consultant consultant) {
        return new ConsultantInfoResponse(
                consultant.getUserId(),
                consultant.getUserNameObject().getLastName(),
                consultant.getUserNameObject().getFirstName(),
                consultant.getGender().toString(),
                consultant.getPhoneNumber(),
                consultant.getAddress(),
                consultant.getMedicalCategory().toString()
        );
    }

    private PatientInfoResponse toPatientInfoResponse(Patient patient) {
        return new PatientInfoResponse(
                patient.getUserId(),
                patient.getUserNameObject().getLastName(),
                patient.getUserNameObject().getFirstName(),
                patient.getPhoneNumber(),
                patient.getGender().toString(),
                patient.getAddress()
        );
    }

    public void createRecommendationForPatient(Patient patient) {
        var medicalCategories = patient.getMedicalCategories();
        var searchHistory = searchService.retrieveSearchesByUserId(patient.getUserId());
        var consultantsBasedOnMedicalCategories = retrieveConsultantsBasedOnMedicalCategories(medicalCategories);

        Set<ConsultantRecommendation> consultantRecommendations = consultantsBasedOnMedicalCategories.stream()
                .map(consultant -> new ConsultantRecommendation(UUID.randomUUID(), consultant, patient))
                .collect(Collectors.toSet());

        var consultantsBasedOnSearchHistory = retrieveConsultantsBasedOnPatientSearchHistory(searchHistory);
        consultantRecommendations.addAll(consultantsBasedOnSearchHistory.stream()
                .map(consultant -> new ConsultantRecommendation(UUID.randomUUID(), consultant, patient))
                .collect(Collectors.toSet()));

        recommendationRepository.saveAll(consultantRecommendations);
    }

    private Set<Consultant> retrieveConsultantsBasedOnMedicalCategories(Set<MedicalCategory> medicalCategories) {
        return medicalCategories.stream()
                .flatMap(medicalCategory -> consultantService
                        .getConsultantByCategory(medicalCategory)
                        .stream())
                .collect(Collectors.toSet());
    }

    private Set<Consultant> retrieveConsultantsBasedOnPatientSearchHistory(Set<Search> searches) {
        Set<MedicalCategory> medicalCategories = new HashSet<>(30);
        for (Search search : searches) {
            for (MedicalCategory medicalCategory : MedicalCategory.values()) {
                if (search.getQuery().matches("[" + medicalCategory.toString().toLowerCase() + "]")) {
                    medicalCategories.add(medicalCategory);
                }
            }
        }
        return retrieveConsultantsBasedOnMedicalCategories(medicalCategories);
    }


}

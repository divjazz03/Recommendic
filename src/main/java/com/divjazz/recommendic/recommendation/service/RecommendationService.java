package com.divjazz.recommendic.recommendation.service;

import com.divjazz.recommendic.recommendation.dto.RecommendationDTO;
import com.divjazz.recommendic.recommendation.model.Recommendation;
import com.divjazz.recommendic.recommendation.model.recommendationAttributes.RecommendationId;
import com.divjazz.recommendic.recommendation.repository.RecommendationRepository;
import com.divjazz.recommendic.search.Search;
import com.divjazz.recommendic.search.SearchService;
import com.divjazz.recommendic.user.enums.MedicalCategory;
import com.divjazz.recommendic.user.model.Patient;
import com.divjazz.recommendic.user.service.ConsultantService;
import com.divjazz.recommendic.user.service.PatientService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendationService {

    private final RecommendationRepository recommendationRepository;
    private final SearchService searchService;
    private final ConsultantService consultantService;
    private final PatientService patientService;

    public RecommendationService(RecommendationRepository recommendationRepository, SearchService searchService, ConsultantService consultantService, PatientService patientService){
        this.recommendationRepository = recommendationRepository;
        this.searchService = searchService;
        this.consultantService = consultantService;
        this.patientService = patientService;
    }

    public Set<RecommendationDTO> retrieveRecommendationByPatient(Patient patient){
        return recommendationRepository.findByPatient(patient)
                .orElse(Collections.emptySet())
                .stream().map(recommendation -> new RecommendationDTO(recommendation.getId(),
                        recommendation.getConsultant(),
                        recommendation.getPatient()))
                .collect(Collectors.toSet());

    }

    public void createRecommendationForPatient(Patient patient){
        Set<Recommendation> recommendations= generateRecommendation(patient);
        patient.setRecommendations(recommendations);
        patientService.modifyPatient(patient);

    }

    private Set<Recommendation> generateRecommendation(Patient patient){
        List<Search> searches = patient.getSearches();
        Set<Recommendation> recommendations = new HashSet<>(20);
        int searchForPEDIATRICIAN = (int) searches.stream()
                .filter(search -> search.getQuery().contains("PEDIATRICIAN"))
                .count();
        if (searchForPEDIATRICIAN > 20){
            recommendations.addAll(consultantService
                    .getConsultantByCategory(MedicalCategory.PEDIATRICIAN).stream()
                    .map(consultant -> new Recommendation(new RecommendationId(UUID.randomUUID()),consultant,patient))
                    .collect(Collectors.toSet()));
        }
        int searchForCARDIOLOGY = (int) searches.stream()
                .filter(search -> search.getQuery().contains("CARDIOLOGY"))
                .count();
        if (searchForCARDIOLOGY > 20){
            recommendations.addAll(consultantService
                    .getConsultantByCategory(MedicalCategory.CARDIOLOGY).stream()
                    .map(consultant -> new Recommendation(new RecommendationId(UUID.randomUUID()),consultant,patient))
                    .collect(Collectors.toSet()));
        }
        int searchForONCOLOGY = (int) searches.stream()
                .filter(search -> search.getQuery().contains("ONCOLOGY"))
                .count();
        if (searchForONCOLOGY > 20){
            recommendations.addAll(consultantService
                    .getConsultantByCategory(MedicalCategory.ONCOLOGY).stream()
                    .map(consultant -> new Recommendation(new RecommendationId(UUID.randomUUID()),consultant,patient))
                    .collect(Collectors.toSet()));
        }
        int searchForDERMATOLOGY = (int) searches.stream()
                .filter(search -> search.getQuery().contains("DERMATOLOGY"))
                .count();
        if(searchForCARDIOLOGY > 20){
            recommendations.addAll(consultantService
                    .getConsultantByCategory(MedicalCategory.DERMATOLOGY).stream()
                    .map(consultant -> new Recommendation(new RecommendationId(UUID.randomUUID()),consultant,patient))
                    .collect(Collectors.toSet()));
        }
        int searchForORTHOPEDICSURGERY = (int) searches.stream()
                .filter(search -> search.getQuery().contains("ORTHOPEDIC_SURGERY"))
                .count();
        if (searchForORTHOPEDICSURGERY > 20){
            recommendations.addAll(consultantService
                    .getConsultantByCategory(MedicalCategory.ORTHOPEDIC_SURGERY).stream()
                    .map(consultant -> new Recommendation(new RecommendationId(UUID.randomUUID()),consultant,patient))
                    .collect(Collectors.toSet()));
        }
        int searchForNEUROSURGERY = (int) searches.stream()
                .filter(search -> search.getQuery().contains("NEUROSURGERY"))
                .count();
        if (searchForNEUROSURGERY > 20){
            recommendations.addAll(consultantService
                    .getConsultantByCategory(MedicalCategory.NEUROSURGERY).stream()
                    .map(consultant -> new Recommendation(new RecommendationId(UUID.randomUUID()),consultant,patient))
                    .collect(Collectors.toSet()));
        }
        int searchForCARDIOVASCULAR_SURGERY = (int) searches.stream()
                .filter(search -> search.getQuery().contains("CARDIOVASCULAR_SURGERY"))
                .count();
        if(searchForCARDIOVASCULAR_SURGERY > 20){
            recommendations.addAll(consultantService
                    .getConsultantByCategory(MedicalCategory.CARDIOVASCULAR_SURGERY).stream()
                    .map(consultant -> new Recommendation(new RecommendationId(UUID.randomUUID()),consultant,patient))
                    .collect(Collectors.toSet()));
        }
        int searchForGYNECOLOGY = (int) searches.stream()
                .filter(search -> search.getQuery().contains("GYNECOLOGY"))
                .count();
        if(searchForGYNECOLOGY > 20){
            recommendations.addAll(consultantService
                    .getConsultantByCategory(MedicalCategory.GYNECOLOGY).stream()
                    .map(consultant -> new Recommendation(new RecommendationId(UUID.randomUUID()),consultant,patient))
                    .collect(Collectors.toSet()));
        }
        int searchForPSYCHIATRY = (int) searches.stream()
                .filter(search -> search.getQuery().contains("PSYCHIATRY"))
                .count();
        if(searchForPSYCHIATRY > 20){
            recommendations.addAll(consultantService
                    .getConsultantByCategory(MedicalCategory.PSYCHIATRY).stream()
                    .map(consultant -> new Recommendation(new RecommendationId(UUID.randomUUID()),consultant,patient))
                    .collect(Collectors.toSet()));
        }
        int searchForDENTISTRY = (int) searches.stream()
                .filter(search -> search.getQuery().contains("DENTISTRY"))
                .count();
        if (searchForDENTISTRY > 20){
            recommendations.addAll(consultantService
                    .getConsultantByCategory(MedicalCategory.DENTISTRY).stream()
                    .map(consultant -> new Recommendation(new RecommendationId(UUID.randomUUID()),consultant,patient))
                    .collect(Collectors.toSet()));
        }
        int searchForOPHTHALMOLOGY = (int) searches.stream()
                .filter(search -> search.getQuery().contains("OPHTHALMOLOGY"))
                .count();
        if (searchForOPHTHALMOLOGY > 20){
            recommendations.addAll(consultantService
                    .getConsultantByCategory(MedicalCategory.OPHTHALMOLOGY).stream()
                    .map(consultant -> new Recommendation(new RecommendationId(UUID.randomUUID()),consultant,patient))
                    .collect(Collectors.toSet()));
        }
        int searchForPHYSICAL_THERAPY = (int) searches.stream()
                .filter(search -> search.getQuery().contains("PHYSICAL_THERAPY"))
                .count();
        if (searchForPHYSICAL_THERAPY > 20){
            recommendations.addAll(consultantService
                    .getConsultantByCategory(MedicalCategory.PHYSICAL_THERAPY).stream()
                    .map(consultant -> new Recommendation(new RecommendationId(UUID.randomUUID()),consultant,patient))
                    .collect(Collectors.toSet()));
        }
        return recommendations;

    }
}

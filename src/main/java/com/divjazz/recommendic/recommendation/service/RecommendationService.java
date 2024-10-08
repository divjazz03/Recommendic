package com.divjazz.recommendic.recommendation.service;

import com.divjazz.recommendic.recommendation.model.Recommendation;
import com.divjazz.recommendic.recommendation.repository.RecommendationRepository;
import com.divjazz.recommendic.user.dto.ConsultantResponse;
import com.divjazz.recommendic.user.dto.PatientResponse;
import com.divjazz.recommendic.user.model.Consultant;
import com.divjazz.recommendic.user.model.Patient;
import com.divjazz.recommendic.user.service.ConsultantService;
import com.divjazz.recommendic.user.service.PatientService;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class RecommendationService {

    private final RecommendationRepository recommendationRepository;
    private final ConsultantService consultantService;
    private final PatientService patientService;

    public RecommendationService(RecommendationRepository recommendationRepository, ConsultantService consultantService, PatientService patientService){
        this.recommendationRepository = recommendationRepository;
        this.consultantService = consultantService;
        this.patientService = patientService;
    }

    public Set<Recommendation> retrieveRecommendationByPatient(Patient patient){
        createRecommendationForPatient(patient);

        return recommendationRepository.findByPatient(patient).orElse(Set.of());

    }
    private ConsultantResponse toConsultantInfoResponse(Consultant consultant){
        return new ConsultantResponse(
                consultant.getUserId(),
                consultant.getUserNameObject().getLastName(),
                consultant.getUserNameObject().getFirstName(),
                consultant.getGender().toString(),
                consultant.getAddress(),
                consultant.getMedicalCategory().toString()
        );
    }
    private PatientResponse toPatientInfoResponse(Patient patient){
        return new PatientResponse(
                patient.getUserId(),
                patient.getUserNameObject().getLastName(),
                patient.getUserNameObject().getFirstName(),
                patient.getPhoneNumber(),
                patient.getGender().toString(),
                patient.getAddress()
        );
    }

    public void createRecommendationForPatient(Patient patient){
        //TODO: IMPLEMENT A RECOMMENDATION ALGORITHM AND REMOVE NULL
        Set<Recommendation> recommendations= null;
        recommendationRepository.saveAll(recommendations);
    }

}

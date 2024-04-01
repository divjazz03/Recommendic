package com.divjazz.recommendic.recommendation.service;

import com.divjazz.recommendic.recommendation.dto.RecommendationDTO;
import com.divjazz.recommendic.recommendation.model.Recommendation;
import com.divjazz.recommendic.recommendation.repository.RecommendationRepository;
import com.divjazz.recommendic.user.model.Patient;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RecommendationService {

    private final RecommendationRepository recommendationRepository;

    public RecommendationService(RecommendationRepository recommendationRepository){
        this.recommendationRepository = recommendationRepository;
    }

    public Set<RecommendationDTO> retrieveRecommendationByPatient(Patient patient){
        return recommendationRepository.findByPatient(patient)
                .orElse(Collections.emptySet())
                .stream().map(recommendation -> new RecommendationDTO(recommendation.getId(),
                        recommendation.getConsultant(),
                        recommendation.getPatient()))
                .collect(Collectors.toSet());

    }
}

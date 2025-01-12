package com.divjazz.recommendic.recommendation.repository;

import com.divjazz.recommendic.recommendation.model.ConsultantRecommendation;
import com.divjazz.recommendic.user.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface RecommendationRepository extends JpaRepository<ConsultantRecommendation, UUID> {
    Optional<Set<ConsultantRecommendation>> findByPatient(Patient patient);
}

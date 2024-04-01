package com.divjazz.recommendic.recommendation.repository;

import com.divjazz.recommendic.recommendation.model.Recommendation;
import com.divjazz.recommendic.recommendation.model.recommendationAttributes.RecommendationId;
import com.divjazz.recommendic.user.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

public interface RecommendationRepository extends JpaRepository<Recommendation, RecommendationId> {
    Optional<Set<Recommendation>> findByPatient(Patient patient);
}

package com.divjazz.recommendic.recommendation.repository;

import com.divjazz.recommendic.recommendation.model.Recommendation;

import com.divjazz.recommendic.user.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface RecommendationRepository extends JpaRepository<Recommendation, UUID> {
    Optional<Set<Recommendation>> findByPatient(Patient patient);
}

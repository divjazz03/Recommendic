package com.divjazz.recommendic.consultation.repository;

import com.divjazz.recommendic.consultation.model.ConsultationSession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConsultationSessionRepository extends JpaRepository<ConsultationSession, Long> {
}

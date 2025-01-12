package com.divjazz.recommendic.consultation.repository;

import com.divjazz.recommendic.consultation.model.Consultation;
import com.divjazz.recommendic.user.model.Consultant;
import com.divjazz.recommendic.user.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface ConsultationRepository extends JpaRepository<Consultation, Long> {
    Optional<Consultation> findByPatientAndConsultant(Patient patient, Consultant consultant);

    @Query(value = "SELECT c from Consultation c where c.patient.userId = :userId or c.consultant.userId = :userId")
    Set<Consultation> getAllConsultationsWhichContainsTheUserId(
            @Param("userId") String userId);

    Set<Consultation> getAllByAccepted(boolean isAccepted);

    Optional<Consultation> getConsultationByConsultationId(String consultationId);

    Set<Consultation> getAllByConsultantAndAccepted(Consultant consultant, boolean isAccepted);


}

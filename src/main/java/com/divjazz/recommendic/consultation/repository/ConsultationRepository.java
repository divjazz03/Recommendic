package com.divjazz.recommendic.consultation.repository;

import com.divjazz.recommendic.consultation.model.Consultation;
import com.divjazz.recommendic.user.model.Consultant;
import com.divjazz.recommendic.user.model.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface ConsultationRepository extends JpaRepository<Consultation, Long> {
    Optional<Consultation> findByPatientAndConsultant(Patient patient, Consultant consultant);

    @Query(value = "SELECT DISTINCT FROM consultation c where c.consultant_id = :consultantId and accepted = :accepted", nativeQuery = true)
    Page<Consultation> findAllByConsultantIdAndAccepted(String consultantId, boolean accepted, Pageable pageable);

    @Query(value = "SELECT DISTINCT from consultation c where c.patient_id = :userId or c.consultant_id = :userId"
            , nativeQuery = true)
    Set<Consultation> getAllConsultationsWhichContainsTheUserId(
            @Param("userId") String userId);

    Set<Consultation> getAllByAccepted(boolean isAccepted);

    Optional<Consultation> getConsultationByConsultationId(String consultationId);

    Set<Consultation> getAllByConsultantAndAccepted(Consultant consultant, boolean isAccepted);

    @Query(value = "select * from consultation where patient_id = :patientId order by created_at ",
            countQuery = "select count(*) from consultation where patient_id = :patientId", nativeQuery = true)
    Page<Consultation> findConsultationsByPatientIdOrderByCreatedAtAsc(String patientId, Pageable pageable);

}

package com.divjazz.recommendic.consultation.repository;

import com.divjazz.recommendic.appointment.model.Appointment;
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
import java.util.stream.Stream;

@Repository
public interface ConsultationRepository extends JpaRepository<Consultation, Long> {
    Optional<Consultation> findByAppointmentId(Long id);
    Optional<Consultation> findByConsultationId(String id);
    boolean existsByAppointmentId(Long id);


    @Query(value = """ 
    SELECT c FROM Consultation c
    WHERE c.appointment.consultant.userId = :consultantId
    """)
    Page<Consultation> findAllByAppointment_Consultant_UserId(@Param("consultantId") String consultantId, Pageable pageable);

    @Query(value = """ 
    SELECT c from Consultation c
    WHERE c.appointment.patient.userId = :userId or c.appointment.consultant.userId = :userId
    """)
    Set<Consultation> getAllConsultationsByAppointment_Patient_UserIdOrAppointment_Consultant_UserId(
            @Param("targetId") String userId);

    @Query(value = """
     select c from Consultation c
     WHERE c.appointment.patient.userId = :patientId
     ORDER BY c.appointment.createdAt
     """
    )
    Page<Consultation> findConsultationsByPatientIdOrderByAppointmentCreatedAt(@Param("patientId") String patientId, Pageable pageable);
    @Query(value = """
    select c from Consultation c
    WHERE c.appointment.patient.userId = :patientId
    """)
    Stream<Consultation> findConsultationsByPatientUserId(@Param("patientId") String patientId);
    @Query(value = """ 
    select c from Consultation c
    WHERE c.appointment.consultant.userId = :consultantId
    """)
    Stream<Consultation> findConsultationsByConsultantUserId(@Param("consultantId") String consultantId);

}

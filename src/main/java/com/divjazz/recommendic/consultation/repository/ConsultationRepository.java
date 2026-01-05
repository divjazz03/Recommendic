package com.divjazz.recommendic.consultation.repository;

import com.divjazz.recommendic.consultation.enums.ConsultationStatus;
import com.divjazz.recommendic.consultation.model.Consultation;
import com.divjazz.recommendic.user.dto.ReviewDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

@Repository
public interface ConsultationRepository extends JpaRepository<Consultation, Long> {
    Optional<Consultation> findByConsultationId(String id);

    boolean existsByAppointment_AppointmentId(String id);

    @Query(
            value = """
                        SELECT new com.divjazz.recommendic.consultation.repository.ConsultationProjection(
                            c.consultationId,
                            c.appointment,
                            c.appointment.patient.patientProfile,
                            c.appointment.consultant.profile,
                            c.summary,
                            c.consultationStatus,
                            c.channel,
                            c.endedAt,
                            c.startedAt
                        ) FROM Consultation c
                        WHERE c.appointment.consultant.userId = :consultantId
                    """
    )
    Set<ConsultationProjection> findConsultationByConsultantId(String consultantId);

    @Query(
            value = """
                        SELECT new com.divjazz.recommendic.consultation.repository.ConsultationProjection(
                            c.consultationId,
                            c.appointment,
                            c.appointment.patient.patientProfile,
                            c.appointment.consultant.profile,
                            c.summary,
                            c.consultationStatus,
                            c.channel,
                            c.endedAt,
                            c.startedAt
                        ) FROM Consultation c
                        WHERE c.appointment.consultant.userId = :patientId
                    """
    )
    Set<ConsultationProjection> findConsultationByPatientId(String patientId);

    @Query(value = """
            SELECT new com.divjazz.recommendic.user.dto.ReviewDTO(
                    c.review.name,
                    c.review.rating,
                    c.review.comment,
                    c.review.date
                    )
                            FROM Consultation c
                                    WHERE c.appointment.consultant.userId = :consultantId
            """)
    Set<ReviewDTO> findReviewsForConsultant(String consultantId);

    @Query("""
            SELECT count(*)
                FROM Consultation c
                WHERE c.appointment.appointmentId in :appointmentIds
                AND c.consultationStatus= :status
            """)
    Long countAllConsultationByAppointment_IdsAndStatus(Set<String> appointmentIds, ConsultationStatus status);
}

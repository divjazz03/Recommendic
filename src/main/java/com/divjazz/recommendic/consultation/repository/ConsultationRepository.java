package com.divjazz.recommendic.consultation.repository;

import com.divjazz.recommendic.consultation.enums.ConsultationStatus;
import com.divjazz.recommendic.consultation.model.Consultation;
import com.divjazz.recommendic.consultation.repository.projection.ConsultationProjection;
import com.divjazz.recommendic.user.dto.ReviewDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

@Repository
public interface ConsultationRepository extends JpaRepository<Consultation, Long> {
    Optional<Consultation> findByConsultationId(String id);

    Optional<Consultation> findConsultationByConsultationId(String consultationId);

    @Query(
            value = """
                        SELECT
                               con.consultationId as consultationId,
                                                pp.dateOfBirth as patientDateOfBirth,
                              p.userId as patientId,
                              pp.userName as patientFullName,
                              c.userId as consultantId,
                              cp.userName as consultantFullName,
                              con.consultationStatus as status,
                              a.appointmentDate as endDate,
                              a.appointmentDate as startDate,
                              s.startTime as startTime,
                              s.endTime as endTime,
                              con.channel as channel,
                              s.zoneOffset as offset,
                              a.history as appointmentHistory,
                              a.reason as reason,
                              con.startedAt as startedAt,
                              con.endedAt as endedAt
                              FROM Consultation con
                              LEFT JOIN Appointment a on a.id = con.appointment.id
                              JOIN a.schedule s
                              JOIN a.patient p
                              JOIN a.consultant c
                              JOIN a.patient.patientProfile pp
                              JOIN a.consultant.profile cp
                              WHERE a.consultant.userId=:consultantId
                    
                    """
    )
    Set<ConsultationProjection> findConsultationByConsultantId(String consultantId);

    @Query(
            value = """
                        SELECT
                    
                              con.consultationId as consultationId,
                                                pp.dateOfBirth as patientDateOfBirth,
                              p.userId as patientId,
                              pp.userName as patientFullName,
                              c.userId as consultantId,
                              cp.userName as consultantFullName,
                              con.consultationStatus as status,
                              a.appointmentDate as endDate,
                              a.appointmentDate as startDate,
                              s.startTime as startTime,
                              s.endTime as endTime,
                              con.channel as channel,
                              s.zoneOffset as offset,
                              a.history as appointmentHistory,
                              a.reason as reason,
                              con.startedAt as startedAt,
                              con.endedAt as endedAt
                              FROM Consultation con
                              LEFT JOIN Appointment a on a.id = con.appointment.id
                              JOIN a.schedule s
                              JOIN a.patient p
                              JOIN a.consultant c
                              JOIN a.patient.patientProfile pp
                              JOIN a.consultant.profile cp
                              WHERE a.patient.userId=:patientId
                    
                    """
    )
    Set<ConsultationProjection> findConsultationByPatientId(String patientId);

    @Query(
            value = """
                        SELECT
                               con.consultationId as consultationId,
                                                pp.dateOfBirth as patientDateOfBirth,
                              p.userId as patientId,
                              pp.userName as patientFullName,
                              c.userId as consultantId,
                              cp.userName as consultantFullName,
                              con.consultationStatus as status,
                              a.appointmentDate as endDate,
                              a.appointmentDate as startDate,
                              s.startTime as startTime,
                              s.endTime as endTime,
                              con.channel as channel,
                              s.zoneOffset as offset,
                              a.history as appointmentHistory,
                              a.reason as reason,
                              con.startedAt as startedAt,
                              con.endedAt as endedAt
                              FROM Consultation con
                              LEFT JOIN Appointment a on a.id = con.appointment.id
                              JOIN a.schedule s
                              JOIN a.patient p
                              JOIN a.consultant c
                              JOIN a.patient.patientProfile pp
                              JOIN a.consultant.profile cp
                              WHERE a.consultant.userId=:consultantId AND a.appointmentDate = :date
                    
                    """
    )
    Set<ConsultationProjection> findConsultationByConsultantIdForDate(String consultantId, LocalDate date);

    @Query(
            value = """
                        SELECT
                               con.consultationId as consultationId,
                                                pp.dateOfBirth as patientDateOfBirth,
                              p.userId as patientId,
                              pp.userName as patientFullName,
                              c.userId as consultantId,
                              cp.userName as consultantFullName,
                              con.consultationStatus as status,
                              a.appointmentDate as endDate,
                              a.appointmentDate as startDate,
                              s.startTime as startTime,
                              s.endTime as endTime,
                              con.channel as channel,
                              s.zoneOffset as offset,
                              a.history as appointmentHistory,
                              a.reason as reason,
                              con.startedAt as startedAt,
                              con.endedAt as endedAt
                              FROM Consultation con
                              LEFT JOIN Appointment a on a.id = con.appointment.id
                              JOIN a.schedule s
                              JOIN a.patient p
                              JOIN a.consultant c
                              JOIN a.patient.patientProfile pp
                              JOIN a.consultant.profile cp
                              WHERE a.patient.userId=:patientId AND a.appointmentDate = :date
                    
                    """
    )
    Set<ConsultationProjection> findConsultationByPatientIdForDate(String patientId, LocalDate date);

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
                WHERE c.consultationId in :consultationIds
                AND c.consultationStatus= :status
            """)
    Long countAllConsultationByConsultationIdsAndStatus(Set<String> consultationIds, ConsultationStatus status);

    @Query("""
            SELECT count(*)
                FROM Consultation con
                LEFT JOIN Appointment a on a.id = con.appointment.id
                JOIN a.consultant c
                WHERE c.userId = :consultantId
                AND a.appointmentDate = :date
            """)
    Long countAllConsultationsByConsultantIdForDate(String consultantId, LocalDate date);
}

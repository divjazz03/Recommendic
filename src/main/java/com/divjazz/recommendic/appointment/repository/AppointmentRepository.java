package com.divjazz.recommendic.appointment.repository;

import com.divjazz.recommendic.appointment.dto.AppointmentDTO;
import com.divjazz.recommendic.appointment.enums.AppointmentStatus;
import com.divjazz.recommendic.appointment.model.Appointment;
import com.divjazz.recommendic.appointment.model.Schedule;
import com.divjazz.recommendic.appointment.repository.projection.AppointmentProjection;
import net.datafaker.providers.base.App;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    @Query("""
            SELECT a FROM Appointment a
            WHERE a.patient.userId = :userId
            ORDER BY a.schedule.startTime DESC
            """)
    Stream<Appointment> findAppointmentsByPatient_UserId(@Param("targetId") String userId);

    @Query(""" 
            SELECT a FROM Appointment a
             WHERE a.consultant.userId = :userId
             ORDER BY a.schedule.startTime DESC
            """)
    Stream<Appointment> findAppointmentsByConsultant_UserId(String userId);

    @Query("""
            SELECT a.appointmentDate FROM Appointment a
            WHERE a.schedule.id = :scheduleId
            ORDER BY a.appointmentDate DESC limit 1
            """)
    LocalDate findLatestAppointmentDateForTheSchedule(Long scheduleId);

    @Query("""
            SELECT a.patient.userId as patientId,
                a.patient.patientProfile.userName as patientFullName,
                a.consultant.userId as consultantId,
                a.consultant.profile.userName as consultantFullName,
                a.status as status,
                a.schedule.startTime as startTime,
                a.schedule.endTime as endTime,
                a.appointmentDate as startDate,
                a.appointmentDate as endDate,
                a.consultationChannel as channel,
                a.schedule.zoneOffset as offset
            FROM Appointment a
            JOIN a.schedule
            JOIN a.consultant
            JOIN a.patient
            JOIN a.patient.patientProfile
            JOIN a.consultant.profile
            WHERE a.appointmentId = :appointmentId
            """)
    Optional<AppointmentProjection> findAppointmentByAppointmentId(String appointmentId);

    List<Appointment> findAppointmentsBySchedule(Schedule schedule);

    List<Appointment> findAppointmentByScheduleAndAppointmentDate(Schedule schedule, LocalDate appointmentDate);

    Set<Appointment> findAllByConsultant_UserIdAndAppointmentDateBetween(String consultantId, LocalDate startDate, LocalDate endDate);

    Set<Appointment> findAllByConsultant_UserIdAndAppointmentDate(String consultantId, LocalDate localDate);

    int countAllByConsultant_UserIdAndAppointmentDate(String consultantId, LocalDate date);

    boolean existsByAppointmentDateAndSchedule_Id(LocalDate date, long id);

    Optional<Appointment> findByAppointmentId(String appointmentId);

    @Modifying
    @Query("""
            UPDATE Appointment a
                SET a.status = :appointmentStatus
                WHERE a.appointmentId = :appointmentId
            """)
    void updateAppointmentStatusByAppointmentId(String appointmentId, AppointmentStatus appointmentStatus);

    @Query("""
            UPDATE Appointment a
                        SET a.status = :appointmentStatus,
                            a.note = :note
                        WHERE a.appointmentId = :appointmentId
            """)
    @Modifying
    void confirmAppointmentStatusAndNotesByAppointmentId(String appointmentId, AppointmentStatus appointmentStatus, String note);

    @Modifying
    @Query("""
                    UPDATE Appointment a
                        SET a.appointmentDate = :newAppointmentDate
                        WHERE a.appointmentId = :appointmentId
            """)
    void updateAppointmentDate(String appointmentId, LocalDate newAppointmentDate);




}

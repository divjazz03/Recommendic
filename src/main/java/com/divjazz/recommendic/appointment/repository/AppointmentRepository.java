package com.divjazz.recommendic.appointment.repository;

import com.divjazz.recommendic.appointment.model.Appointment;
import com.divjazz.recommendic.appointment.model.Schedule;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    @Query("""
    SELECT a FROM Appointment a
    WHERE a.patient.userId = :userId
    ORDER BY a.schedule.startTime DESC
    """)
    @Transactional(readOnly = true)
    Stream<Appointment> findAppointmentsByPatient_UserId(@Param("targetId") String userId);

    @Query(""" 
    SELECT a FROM Appointment a
     WHERE a.consultant.userId = :userId
     ORDER BY a.schedule.startTime DESC
    """)
    @Transactional(readOnly = true)
    Stream<Appointment> findAppointmentsByConsultant_UserId(String userId);

    @Query("""
    SELECT a.appointmentDate FROM Appointment a
    WHERE a.schedule.id = :scheduleId
    ORDER BY a.appointmentDate DESC limit 1
    """)
    LocalDate findLatestAppointmentDateForTheSchedule(Long scheduleId);

    List<Appointment> findAppointmentsBySchedule(Schedule schedule);
}

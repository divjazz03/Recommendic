package com.divjazz.recommendic.appointment.repository;

import com.divjazz.recommendic.appointment.model.Appointment;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.stream.Stream;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    @Query("""
    SELECT a FROM Appointment a
    LEFT JOIN FETCH a.patient p
    LEFT JOIN FETCH a.consultant co
    LEFT JOIN FETCH a.scheduleSlot ss
    WHERE a.patient.userId = :userId
    ORDER BY ss.startTime DESC
    """)
    Stream<Appointment> findAppointmentsByPatient_UserId(@Param("userId") String userId);

    @Query(""" 
    SELECT a FROM Appointment a
     LEFT JOIN FETCH a.consultant co
     LEFT JOIN FETCH a.scheduleSlot ss
     LEFT JOIN FETCH a.patient p
     WHERE a.consultant.userId = :userId
     ORDER BY ss.startTime DESC
    """)
    Stream<Appointment> findAppointmentsByConsultant_UserId(String userId);
}

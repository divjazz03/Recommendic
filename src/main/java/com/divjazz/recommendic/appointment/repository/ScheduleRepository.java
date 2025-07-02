package com.divjazz.recommendic.appointment.repository;

import com.divjazz.recommendic.appointment.model.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.stream.Stream;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    Stream<Schedule> findAllByConsultant_UserId(String consultantId);
}

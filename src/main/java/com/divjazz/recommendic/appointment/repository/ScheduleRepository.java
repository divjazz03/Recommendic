package com.divjazz.recommendic.appointment.repository;

import com.divjazz.recommendic.appointment.model.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    @Transactional(readOnly = true)
    Set<Schedule> findAllByConsultant_UserId(String consultantId);
    Optional<Schedule> findByScheduleId(String scheduleId);
}

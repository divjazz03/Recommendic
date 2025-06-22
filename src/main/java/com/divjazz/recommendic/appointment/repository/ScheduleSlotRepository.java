package com.divjazz.recommendic.appointment.repository;

import com.divjazz.recommendic.appointment.model.ScheduleSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.stream.Stream;

@Repository
public interface ScheduleSlotRepository extends JpaRepository<ScheduleSlot, Long> {

    Stream<ScheduleSlot> findAllByConsultant_UserId(String consultantId);
}

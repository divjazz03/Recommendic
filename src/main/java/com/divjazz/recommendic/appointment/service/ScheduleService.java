package com.divjazz.recommendic.appointment.service;

import com.divjazz.recommendic.appointment.model.Schedule;
import com.divjazz.recommendic.appointment.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    @Transactional(readOnly = true)
    public Stream<Schedule> getScheduleSlotsForConsultants(String consultantId) {
        return scheduleRepository.findAllByConsultant_UserId(consultantId);
    }

}

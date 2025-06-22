package com.divjazz.recommendic.appointment.service;

import com.divjazz.recommendic.appointment.model.ScheduleSlot;
import com.divjazz.recommendic.appointment.repository.ScheduleSlotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ScheduleSlotService {

    private final ScheduleSlotRepository scheduleSlotRepository;

    @Transactional(readOnly = true)
    public Stream<ScheduleSlot> getScheduleSlotsForConsultants(String consultantId) {
        return scheduleSlotRepository.findAllByConsultant_UserId(consultantId);
    }

}

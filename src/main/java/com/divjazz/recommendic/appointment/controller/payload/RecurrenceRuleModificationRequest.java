package com.divjazz.recommendic.appointment.controller.payload;

import com.divjazz.recommendic.appointment.domain.RecurrenceFrequency;
import com.divjazz.recommendic.appointment.validation.schedule.annotation.ScheduleDate;
import com.divjazz.recommendic.appointment.validation.schedule.annotation.ScheduleDayOfWeeks;
import jakarta.validation.constraints.NotNull;

import java.util.Set;


public record RecurrenceRuleModificationRequest(
        @NotNull
        RecurrenceFrequency frequency,
        @ScheduleDayOfWeeks
        Set<String> weekDays,
        int interval,
        @ScheduleDate
        String endDate
) {
}

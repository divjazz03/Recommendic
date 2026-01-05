package com.divjazz.recommendic.appointment.controller.payload;

import com.divjazz.recommendic.appointment.domain.RecurrenceFrequency;
import com.divjazz.recommendic.global.validation.annotation.ValidDate;
import jakarta.validation.constraints.NotNull;

import java.util.Set;


public record RecurrenceRuleModificationRequest(
        @NotNull
        RecurrenceFrequency frequency,
        Set<String> weekDays,
        int interval,
        @ValidDate
        String endDate
) {
}

package com.divjazz.recommendic.medication.utils;

import com.divjazz.recommendic.medication.constants.DurationType;

import java.time.LocalDate;

public final class PrescriptionUtils {
    public static LocalDate getEndDate(LocalDate startDate, int durationValue, DurationType durationType) {
        return switch (durationType) {
            case DAY -> startDate.plusDays(durationValue);
            case WEEK -> startDate.plusWeeks(durationValue);
            case MONTH -> startDate.plusMonths(durationValue);
            case YEAR -> startDate.plusYears(durationValue);
        };
    }
}

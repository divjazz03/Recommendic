package com.divjazz.recommendic.appointment.domain;

import java.util.Set;

public record Availability(
        Set<String> today,
        Set<String> tomorrow,
        Set<String> thisWeek,
        Set<String> booked
) {

}

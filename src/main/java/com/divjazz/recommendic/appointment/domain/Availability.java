package com.divjazz.recommendic.appointment.domain;

import java.util.Set;

public record Availability(
        Set<Slot> today,
        Set<Slot> tomorrow,
        Set<Slot> thisWeek,
        Set<Slot> booked
) {

}

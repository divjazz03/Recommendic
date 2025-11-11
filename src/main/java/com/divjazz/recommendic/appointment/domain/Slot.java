package com.divjazz.recommendic.appointment.domain;

import java.time.LocalDateTime;

public record Slot(String scheduleId, String dateTime) implements Comparable<Slot> {

    @Override
    public int compareTo(Slot o) {
        return LocalDateTime.parse(dateTime).compareTo(LocalDateTime.parse(o.dateTime));
    }
}

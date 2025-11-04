package com.divjazz.recommendic.appointment.domain;

import java.time.OffsetDateTime;

public record Slot(String scheduleId, String dateTime) implements Comparable<Slot> {

    @Override
    public int compareTo(Slot o) {
        return OffsetDateTime.parse(dateTime).compareTo(OffsetDateTime.parse(o.dateTime));
    }
}

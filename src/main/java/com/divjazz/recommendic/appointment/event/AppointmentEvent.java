package com.divjazz.recommendic.appointment.event;

import com.divjazz.recommendic.appointment.enums.AppointmentEventType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentEvent {
    private AppointmentEventType appointmentEventType;
    private Map<?,?> data;
}

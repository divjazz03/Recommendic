package com.divjazz.recommendic.appointment.model;

import com.divjazz.recommendic.Auditable;
import com.divjazz.recommendic.appointment.enums.AppointmentStatus;
import com.divjazz.recommendic.user.model.Consultant;
import com.divjazz.recommendic.user.model.Patient;
import jakarta.persistence.*;
import lombok.*;

@Table(name = "appointment")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Appointment extends Auditable {

    @ManyToOne (optional = false)
    @JoinColumn(name = "patient_id")
    private Patient patient;
    @ManyToOne(optional = false)
    @JoinColumn(name = "consultant_id")
    private Consultant consultant;
    @ManyToOne(optional = false)
    private ScheduleSlot scheduleSlot;
    @Column(name = "note")
    private String note;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private AppointmentStatus status;
}

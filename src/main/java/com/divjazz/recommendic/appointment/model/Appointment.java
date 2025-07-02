package com.divjazz.recommendic.appointment.model;

import com.divjazz.recommendic.global.Auditable;
import com.divjazz.recommendic.appointment.enums.AppointmentStatus;
import com.divjazz.recommendic.consultation.enums.ConsultationChannel;
import com.divjazz.recommendic.user.model.Consultant;
import com.divjazz.recommendic.user.model.Patient;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Table(name = "appointment")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Appointment extends Auditable {

    @ManyToOne(optional = false)
    @JoinColumn(name = "patient_id")
    private Patient patient;
    @ManyToOne(optional = false)
    @JoinColumn(name = "consultant_id")
    private Consultant consultant;
    @ManyToOne(optional = false)
    @JoinColumn(name = "schedule_slot_id")
    private Schedule schedule;
    @Column(name = "note")
    private String note;
    @Column(name = "date")
    private LocalDate appointmentDate;
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private AppointmentStatus status;
    @Enumerated(EnumType.STRING)
    @Column(name = "selected_channel")
    private ConsultationChannel consultationChannel;


    public OffsetDateTime getStartDateAndTime() {
        return OffsetDateTime.of(appointmentDate, schedule.getStartTime(), schedule.getZoneOffset());
    }
    public OffsetDateTime getEndDateAndTime() {
        return OffsetDateTime.of(appointmentDate, schedule.getEndTime(), schedule.getZoneOffset());
    }
}

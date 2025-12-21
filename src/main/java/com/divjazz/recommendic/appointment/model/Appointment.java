package com.divjazz.recommendic.appointment.model;

import com.divjazz.recommendic.appointment.enums.AppointmentHistory;
import com.divjazz.recommendic.appointment.enums.AppointmentStatus;
import com.divjazz.recommendic.consultation.enums.ConsultationChannel;
import com.divjazz.recommendic.global.Auditable;
import com.divjazz.recommendic.user.model.Consultant;
import com.divjazz.recommendic.user.model.Patient;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.generator.EventType;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Table(name = "appointment")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Appointment extends Auditable {
    @Column(name = "appointment_id", updatable = false, insertable = false)
    @Generated(event = EventType.INSERT)
    private String appointmentId;
    @ManyToOne(optional = false)
    @JoinColumn(name = "patient_id")
    private Patient patient;
    @ManyToOne(optional = false)
    @JoinColumn(name = "consultant_id", updatable = false)
    private Consultant consultant;
    @ManyToOne(optional = false)
    @JoinColumn(name = "schedule_slot_id", updatable = false)
    private Schedule schedule;
    @Column(name = "note")
    private String note;
    @Column(name = "date")
    private LocalDate appointmentDate;
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "status")
    private AppointmentStatus status;
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "selected_channel")
    private ConsultationChannel consultationChannel;
    @Column(name = "reason")
    private String reason;
    @Column(name = "history")
    @Enumerated(EnumType.STRING)
    private AppointmentHistory history;


    public LocalDateTime getStartDateAndTime() {
        return LocalDateTime.of(appointmentDate, schedule.getStartTime());
    }
    public LocalDateTime getEndDateAndTime() {
        return LocalDateTime.of(appointmentDate, schedule.getEndTime());
    }

}

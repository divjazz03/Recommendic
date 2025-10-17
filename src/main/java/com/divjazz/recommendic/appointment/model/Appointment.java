package com.divjazz.recommendic.appointment.model;

import com.divjazz.recommendic.global.Auditable;
import com.divjazz.recommendic.appointment.enums.AppointmentStatus;
import com.divjazz.recommendic.consultation.enums.ConsultationChannel;
import com.divjazz.recommendic.user.model.Consultant;
import com.divjazz.recommendic.user.model.Patient;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.generator.EventType;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Objects;

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
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "status")
    private AppointmentStatus status;
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "selected_channel")
    private ConsultationChannel consultationChannel;


    public OffsetDateTime getStartDateAndTime() {
        return OffsetDateTime.of(appointmentDate, schedule.getStartTime(), schedule.getZoneOffset());
    }
    public OffsetDateTime getEndDateAndTime() {
        return OffsetDateTime.of(appointmentDate, schedule.getEndTime(), schedule.getZoneOffset());
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        Appointment that = (Appointment) o;
        return Objects.equals(schedule.getScheduleId(), that.schedule.getScheduleId()) && Objects.equals(appointmentDate, that.appointmentDate);
    }

    @Override
    public int hashCode() {
        return 2024;
    }
}

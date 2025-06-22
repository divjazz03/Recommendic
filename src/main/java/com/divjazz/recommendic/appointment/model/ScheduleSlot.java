package com.divjazz.recommendic.appointment.model;

import com.divjazz.recommendic.Auditable;
import com.divjazz.recommendic.consultation.enums.ConsultationChannel;
import com.divjazz.recommendic.user.model.Consultant;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;

@Entity
@Table(name = "schedule_slot")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleSlot extends Auditable {
    @ManyToOne
    @JoinColumn(name = "consultant_id")
    private Consultant consultant;
    @Column(name = "start_time")
    private ZonedDateTime startTime;
    @Column(name = "end_time")
    private ZonedDateTime endTime;
    @Enumerated(EnumType.STRING)
    private ConsultationChannel consultationChannel;
    @Column(name = "is_recurring")
    private boolean isRecurring;
    @Column (name = "recurrence_rule")
    private String recurrenceRule;
    @Column (name = "is_booked")
    private boolean isBooked;
}

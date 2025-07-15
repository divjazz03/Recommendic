package com.divjazz.recommendic.appointment.model;

import com.divjazz.recommendic.global.Auditable;
import com.divjazz.recommendic.consultation.enums.ConsultationChannel;
import com.divjazz.recommendic.global.converter.ZoneOffsetConverter;
import com.divjazz.recommendic.user.model.Consultant;
import io.hypersistence.utils.hibernate.type.array.EnumArrayType;
import io.hypersistence.utils.hibernate.type.array.internal.AbstractArrayType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;

import java.time.LocalTime;
import java.time.ZoneOffset;

@Entity
@Table(name = "schedule_slot")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Schedule extends Auditable {
    @ManyToOne
    @JoinColumn(name = "consultant_id")
    private Consultant consultant;
    @Column(name = "name")
    private String name;
    @Column(name = "start_time")
    private LocalTime startTime;
    @Column(name = "end_time")
    private LocalTime endTime;
    @Column(name = "utf_offset")
    @Convert(converter = ZoneOffsetConverter.class)
    private ZoneOffset zoneOffset;
    @Type(value = EnumArrayType.class,
    parameters = @org.hibernate.annotations.Parameter(name = AbstractArrayType.SQL_ARRAY_TYPE,
            value = "session_channel"))
    @Column(name = "consultation_channel")
    private ConsultationChannel[] consultationChannels;
    @Column(name = "is_recurring")
    private boolean isRecurring;
    @Column (name = "recurrence_rule")
    private String recurrenceRule;
    @Column (name = "is_active")
    boolean isActive;

}

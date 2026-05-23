package com.divjazz.recommendic.appointment.model;

import com.divjazz.recommendic.appointment.domain.RecurrenceRule;
import com.divjazz.recommendic.global.Auditable;
import com.divjazz.recommendic.consultation.enums.ConsultationChannel;
import com.divjazz.recommendic.global.converter.ZoneOffsetConverter;
import com.divjazz.recommendic.user.model.Consultant;
import com.github.f4b6a3.ulid.UlidCreator;
import io.hypersistence.utils.hibernate.type.array.EnumArrayType;
import io.hypersistence.utils.hibernate.type.array.StringArrayType;
import io.hypersistence.utils.hibernate.type.array.internal.AbstractArrayType;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.generator.EventType;

import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.Set;

@Entity
@Table(name = "schedule_slots")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Schedule extends Auditable {

    @ManyToOne
    @JoinColumn(name = "consultant_id")
    private Consultant consultant;
    @Column(name = "schedule_id", updatable = false)
    private final String scheduleId = "SCH-" + UlidCreator.getMonotonicUlid().toString();
    @Column(name = "name")
    private String name;
    @Column(name = "start_time")
    private LocalTime startTime;
    @Column(name = "end_time")
    private LocalTime endTime;
    @Column(name = "utf_offset")
    @Convert(converter = ZoneOffsetConverter.class)
    private ZoneOffset zoneOffset;
    @Type(StringArrayType.class)
    @Column(name = "consultation_channels", columnDefinition = "text[]")
    private String[] consultationChannels;
    @Column (name = "recurrence_rule")
    @Type(JsonBinaryType.class)
    private RecurrenceRule recurrenceRule;
    @Column (name = "is_active")
    private boolean isActive;
}

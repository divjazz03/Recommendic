package com.divjazz.recommendic.consultation.model;

import com.divjazz.recommendic.global.Auditable;
import com.divjazz.recommendic.appointment.model.Appointment;
import com.divjazz.recommendic.consultation.enums.ConsultationChannel;
import com.divjazz.recommendic.consultation.enums.ConsultationStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.f4b6a3.ulid.UlidCreator;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.generator.EventType;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "consultations")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Consultation extends Auditable {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id")
    private Appointment appointment;
    @Column(name = "summary")
    private String summary;
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ConsultationStatus consultationStatus;
    @Column(name = "started_at")
    private LocalDateTime startedAt;
    @Column(name = "ended_at")
    private LocalDateTime endedAt;
    @Column (name = "channel")
    @Enumerated(EnumType.STRING)
    private ConsultationChannel channel;
    @Column(name = "consultation_id", updatable = false)
    private final String consultationId = "CSTN-"+ UlidCreator.getMonotonicUlid();
    @OneToOne(mappedBy = "consultation")
    @JsonIgnore
    private ConsultationReview review;
    @ManyToOne
    @JoinColumn(name = "session_id")
    private ConsultationSession session;

    public Consultation(Appointment appointment, ConsultationChannel channel) {
        this.appointment = appointment;
        this.channel = channel;
        this.consultationStatus = ConsultationStatus.ONGOING;
        this.startedAt = LocalDateTime.now();
    }
}

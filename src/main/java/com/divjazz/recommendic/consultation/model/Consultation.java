package com.divjazz.recommendic.consultation.model;

import com.divjazz.recommendic.global.Auditable;
import com.divjazz.recommendic.appointment.model.Appointment;
import com.divjazz.recommendic.consultation.enums.ConsultationChannel;
import com.divjazz.recommendic.consultation.enums.ConsultationStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "consultation")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Consultation extends Auditable {

    @OneToOne
    @JoinColumn(name = "appointment_id")
    private Appointment appointment;
    @Column(name = "summary")
    private String summary;
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private ConsultationStatus consultationStatus;
    @Column(name = "started_at")
    private LocalDateTime startedAt;
    @Column(name = "ended_at")
    private LocalDateTime endedAt;
    @Column (name = "channel")
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private ConsultationChannel channel;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Consultation that = (Consultation) o;

        return Objects.equals(appointment.getId(), that.getAppointment().getId());
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (appointment != null ? appointment.hashCode() : 0);
        return result;
    }
}

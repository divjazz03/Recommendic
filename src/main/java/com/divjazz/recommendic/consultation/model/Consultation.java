package com.divjazz.recommendic.consultation.model;

import com.divjazz.recommendic.Auditable;
import com.divjazz.recommendic.appointment.model.Appointment;
import com.divjazz.recommendic.consultation.enums.ConsultationChannel;
import com.divjazz.recommendic.consultation.enums.ConsultationStatus;
import jakarta.persistence.*;
import lombok.*;

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
    private ConsultationStatus consultationStatus;
    @Column(name = "started_at")
    private LocalDateTime startedAt;
    @Column(name = "ended_at")
    private LocalDateTime endedAt;
    @Column (name = "channel")
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

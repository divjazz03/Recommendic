package com.divjazz.recommendic.recommendation.model;

import com.divjazz.recommendic.Auditable;
import com.divjazz.recommendic.user.model.Consultant;
import com.divjazz.recommendic.user.model.Patient;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "consultant_recommendation")
public class ConsultantRecommendation extends Auditable {
    @ManyToOne()
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @ManyToOne()
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "consultant_id")
    private Consultant consultant;


    public ConsultantRecommendation(UUID id, Consultant consultant, Patient patient) {
        this.consultant = consultant;
        this.patient = patient;
    }

    protected ConsultantRecommendation() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        ConsultantRecommendation that = (ConsultantRecommendation) o;

        if (!Objects.equals(patient, that.patient)) return false;
        return Objects.equals(consultant, that.consultant);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (patient != null ? patient.hashCode() : 0);
        result = 31 * result + (consultant != null ? consultant.hashCode() : 0);
        return result;
    }
}

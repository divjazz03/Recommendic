package com.divjazz.recommendic.recommendation.model;

import com.divjazz.recommendic.Auditable;

import com.divjazz.recommendic.user.model.Consultant;
import com.divjazz.recommendic.user.model.Patient;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Entity
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Table(name = "recommendation")
public class Recommendation extends Auditable implements Serializable {
    @ManyToOne()
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @ManyToOne()
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "consultant_id")
    private Consultant consultant;
    //Todo: Create a recommendation system for Articles


    public Recommendation(UUID id, Consultant consultant, Patient patient) {
        this.consultant = consultant;
        this.patient = patient;
    }
    protected Recommendation(){}

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Consultant getConsultant() {
        return consultant;
    }

    public void setConsultant(Consultant consultant) {
        this.consultant = consultant;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Recommendation that = (Recommendation) o;

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

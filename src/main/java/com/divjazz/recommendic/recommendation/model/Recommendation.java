package com.divjazz.recommendic.recommendation.model;

import com.divjazz.recommendic.Auditable;

import com.divjazz.recommendic.user.model.Consultant;
import com.divjazz.recommendic.user.model.Patient;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.UUID;

@Entity
public class Recommendation extends Auditable {

    @ManyToOne(optional = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Patient patient;

    @ManyToOne()
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Consultant consultant;
    //Todo: Create a recommendation system for Articles


    public Recommendation(UUID id, Consultant consultant, Patient patient) {
        this.consultant = consultant;
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

}

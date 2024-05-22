package com.divjazz.recommendic.recommendation.model;

import com.divjazz.recommendic.recommendation.model.recommendationAttributes.RecommendationId;
import com.divjazz.recommendic.user.model.Consultant;
import com.divjazz.recommendic.user.model.Patient;
import io.github.wimdeblauwe.jpearl.AbstractEntity;
import jakarta.persistence.*;

import java.util.Objects;
import java.util.Set;

@Entity
public class Recommendation extends AbstractEntity<RecommendationId> {

    @ManyToOne
    private Patient patient;

    @ManyToOne
    private Consultant consultant;
    //Todo: Create a recommendation system for Articles


    public Recommendation(RecommendationId id, Consultant consultant, Patient patient) {
        super(id);
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

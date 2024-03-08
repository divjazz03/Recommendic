package com.divjazz.recommendic.recommendation.model;

import com.divjazz.recommendic.recommendation.model.recommendationAttributes.RecommendationId;
import com.divjazz.recommendic.user.model.Consultant;
import com.divjazz.recommendic.user.model.Patient;
import io.github.wimdeblauwe.jpearl.AbstractEntity;
import jakarta.persistence.*;

import java.util.Set;

@Entity
public class Recommendation extends AbstractEntity<RecommendationId> {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="consultant_id")
    private Consultant consultant;
    //Todo: Create a recommendation system for Articles


    public Recommendation(RecommendationId id, Consultant consultant) {
        super(id);
        this.consultant = consultant;
    }
    protected Recommendation(){}
}

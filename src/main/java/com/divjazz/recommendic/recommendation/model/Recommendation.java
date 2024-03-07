package com.divjazz.recommendic.recommendation.model;

import com.divjazz.recommendic.recommendation.model.recommendationAttributes.RecommendationId;
import com.divjazz.recommendic.user.model.Consultant;
import com.divjazz.recommendic.user.model.Patient;
import io.github.wimdeblauwe.jpearl.AbstractEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

import java.util.Set;

@Entity
public class Recommendation extends AbstractEntity<RecommendationId> {

    @ManyToOne
    private Patient patient;
    @OneToMany

    private Set<Consultant> consultants;
    //Todo: Create a recommendation system for Articles


    public Recommendation(RecommendationId id, Set<Consultant> consultants) {
        super(id);
        this.consultants = consultants;
    }
    protected Recommendation(){}
}

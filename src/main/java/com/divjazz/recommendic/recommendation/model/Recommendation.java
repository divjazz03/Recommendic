package com.divjazz.recommendic.recommendation.model;

import com.divjazz.recommendic.recommendation.model.recommendationAttributes.RecommendationId;
import com.divjazz.recommendic.user.model.Consultant;
import com.divjazz.recommendic.user.model.Patient;
import io.github.wimdeblauwe.jpearl.AbstractEntity;
import jakarta.persistence.*;

import java.util.Set;


public class Recommendation {


    private Patient patient;

    private Consultant consultant;
    //Todo: Create a recommendation system for Articles


    public Recommendation(RecommendationId id, Consultant consultant) {
        this.consultant = consultant;
    }
    protected Recommendation(){}
}

package com.divjazz.recommendic.user.model;

import com.divjazz.recommendic.recommendation.model.Recommendation;
import com.divjazz.recommendic.user.model.userAttributes.*;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import java.util.Set;

@Entity
public class Patient extends User{

    @OneToMany
    @Cascade(CascadeType.ALL)
    private Set<Recommendation> recommendations;
    @ManyToMany
    private Set<Consultant> consultants;

    protected Patient(){}

    public Patient(UserId id, UserName userName, Email email, PhoneNumber phoneNumber, Gender gender) {
        super(id, userName, email, phoneNumber, gender);
    }

    public Set<Recommendation> getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(Set<Recommendation> recommendations){
        addRecommendations(recommendations);
    }
    private void addRecommendations(Set<Recommendation> recommendations){
        this.recommendations.addAll(recommendations);
    }
}

package com.divjazz.recommendic.user.model;

import com.divjazz.recommendic.recommendation.model.Recommendation;
import com.divjazz.recommendic.user.model.userAttributes.*;

import io.github.wimdeblauwe.jpearl.AbstractEntity;
import jakarta.persistence.*;

import java.util.Set;

@Entity
public class Patient extends AbstractEntity<UserId> {

    @OneToMany(targetEntity = Recommendation.class, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Recommendation> recommendations;
    @ManyToMany(fetch = FetchType.LAZY, targetEntity = Consultant.class)
    private Set<Consultant> consultants;
    @OneToOne(targetEntity = User.class, optional = false)
    @JoinColumn(name = "tt_user_id", nullable = false)
    private User user;



    protected Patient(){}

    public Patient(UserId id, User user){
        super(id);
        this.user = user;
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
    public void setConsultants(Set<Consultant> consultants){
        this.consultants.addAll(consultants);
    }
    public Set<Consultant> getConsultants(){return consultants;}

    public User getUser() {
        return user;
    }

    @Override
    public String toString() {
        return "Patient: name -> " + user.getUserNameObject().getFullName() +
                "email -> " + user.getEmail() +
                "gender -> " + user.getGender().name();
    }
}

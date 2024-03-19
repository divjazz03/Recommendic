package com.divjazz.recommendic.user.model;

import com.divjazz.recommendic.recommendation.model.Recommendation;
import com.divjazz.recommendic.user.UserType;
import com.divjazz.recommendic.user.model.userAttributes.*;

import jakarta.persistence.*;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;


public class Patient{


    private Set<Recommendation> recommendations;
    private Set<Consultant> consultants;



    protected Patient(){}


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

}

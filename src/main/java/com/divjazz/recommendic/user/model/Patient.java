package com.divjazz.recommendic.user.model;

import com.divjazz.recommendic.recommendation.model.Recommendation;
import com.divjazz.recommendic.search.Search;
import com.divjazz.recommendic.user.enums.MedicalCategory;
import com.divjazz.recommendic.user.model.userAttributes.*;

import io.github.wimdeblauwe.jpearl.AbstractEntity;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class Patient extends AbstractEntity<UserId> {

    @OneToMany(targetEntity = Recommendation.class, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Recommendation> recommendations;
    @ManyToMany(fetch = FetchType.LAZY, targetEntity = Consultant.class)
    private Set<Consultant> consultants;
    @OneToOne(targetEntity = User.class, optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "tt_user_id", nullable = false)
    private User user;

    @Enumerated(value = EnumType.STRING)
    private Set<MedicalCategory> medicalCategories;
    @OneToMany
    private List<Search> searches;



    protected Patient(){}

    public Patient(UserId id, User user){
        super(id);
        this.user = user;
        medicalCategories = new HashSet<>();
    }
    public Patient(UserId id, User user, Set<MedicalCategory> medicalCategories){
        super(id);
        this.user = user;
        this.medicalCategories = medicalCategories;

    }

    public Set<MedicalCategory> getMedicalCategories() {
        return medicalCategories;
    }

    public void setMedicalCategories(Set<MedicalCategory> medicalCategories) {
        if (this.medicalCategories == null){
            medicalCategories = medicalCategories;
        }else {
            this.medicalCategories.addAll(medicalCategories);
        }
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
    public List<Search> getSearches(){
        return this.searches;
    }

    @Override
    public String toString() {
        return "Patient: name -> " + user.getUserNameObject().getFullName() +
                "email -> " + user.getEmail() +
                "gender -> " + user.getGender().name();
    }
}

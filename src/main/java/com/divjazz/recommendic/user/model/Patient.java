package com.divjazz.recommendic.user.model;

import com.divjazz.recommendic.recommendation.model.Recommendation;
import com.divjazz.recommendic.search.Search;
import com.divjazz.recommendic.user.enums.Gender;
import com.divjazz.recommendic.user.enums.MedicalCategory;
import com.divjazz.recommendic.user.model.userAttributes.*;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.*;

@Entity
public final class Patient extends User {

    @OneToMany(targetEntity = Recommendation.class, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Recommendation> recommendations;
    @ManyToMany(fetch = FetchType.LAZY, targetEntity = Consultant.class)
    private Set<Consultant> consultants;

    @Enumerated(value = EnumType.STRING)
    private Set<MedicalCategory> medicalCategories;
    @OneToMany
    private List<Search> searches;



    protected Patient(){}

    public Patient(UUID id,
                   UserName userName,
                   String email,
                   String phoneNumber,
                   Gender gender,
                   Address address,
                   String password){

        super(id,userName,email,phoneNumber,gender,address,password);
        medicalCategories = new HashSet<>(30);
        consultants = new HashSet<>(30);
        recommendations = new HashSet<>(30);
    }


    public Set<MedicalCategory> getMedicalCategories() {
        return medicalCategories;
    }

    public void setMedicalCategories(Set<MedicalCategory> medicalCategories) {
        if (this.medicalCategories == null){
            this.medicalCategories = Objects.requireNonNull(medicalCategories);
        }else {
            this.medicalCategories.addAll(Objects.requireNonNull(medicalCategories));
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


    public List<Search> getSearches(){
        return this.searches;
    }

    @Override
    public String toString() {
        return "Patient{" +
                "recommendations=" + recommendations +
                ", consultants=" + consultants +
                ", medicalCategories=" + medicalCategories +
                ", searches=" + searches +
                '}';
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("PATIENT"));
    }
}

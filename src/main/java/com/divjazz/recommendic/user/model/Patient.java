package com.divjazz.recommendic.user.model;

import com.divjazz.recommendic.recommendation.model.Recommendation;
import com.divjazz.recommendic.search.Search;
import com.divjazz.recommendic.user.enums.Gender;
import com.divjazz.recommendic.user.enums.MedicalCategory;
import com.divjazz.recommendic.user.model.userAttributes.*;

import com.divjazz.recommendic.user.model.userAttributes.credential.PatientCredential;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.*;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_DEFAULT;

@Entity
@JsonInclude(NON_DEFAULT)
public final class Patient extends User {

    @OneToMany(targetEntity = Recommendation.class, fetch = FetchType.LAZY, mappedBy = "patient")
    private Set<Recommendation> recommendations;
    @ManyToMany(mappedBy = "patients")
    private Set<Consultant> consultants;

    @Enumerated(value = EnumType.STRING)
    private Set<MedicalCategory> medicalCategories;
    @OneToMany
    private List<Search> searches;

    private String password;
    @OneToOne
    private PatientCredential credential;



    protected Patient(){}

    public Patient(UUID id,
                   UserName userName,
                   String email,
                   String phoneNumber,
                   Gender gender,
                   Address address){

        super(userName,email,phoneNumber,gender,address);
        setId(id);
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

    @Override
    public String getPassword() {
        return this.credential.getPassword();
    }

    public void setCredential(PatientCredential credential) {
        this.credential = credential;
    }

    public PatientCredential getCredential() {
        return credential;
    }
}


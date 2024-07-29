package com.divjazz.recommendic.user.model;

import com.divjazz.recommendic.user.enums.Gender;
import com.divjazz.recommendic.user.enums.MedicalCategory;
import com.divjazz.recommendic.user.model.userAttributes.Address;
import com.divjazz.recommendic.user.model.userAttributes.UserName;
import com.divjazz.recommendic.user.model.userAttributes.credential.PatientCredential;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.*;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_DEFAULT;

@Entity
@JsonInclude(NON_DEFAULT)
@Table(name = "patient")
public final class Patient extends User {


    @ManyToMany(mappedBy = "patients", fetch = FetchType.LAZY)
    private Set<Consultant> consultants;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "medical_categories")
    private Set<MedicalCategory> medicalCategories;

    @OneToOne(mappedBy = "patient", fetch = FetchType.EAGER)
    private PatientCredential credential;



    protected Patient(){}

    public Patient(
                   UserName userName,
                   String email,
                   String phoneNumber,
                   Gender gender,
                   Address address){

        super(userName,email,phoneNumber,gender,address);
        medicalCategories = new HashSet<>(30);
        consultants = new HashSet<>(30);
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

    public void setConsultants(Set<Consultant> consultants){
        this.consultants.addAll(consultants);
    }
    public Set<Consultant> getConsultants(){return consultants;}


    @Override
    public String toString() {
        return "Patient{" +
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


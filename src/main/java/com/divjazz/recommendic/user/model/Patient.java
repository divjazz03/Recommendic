package com.divjazz.recommendic.user.model;

import com.divjazz.recommendic.user.enums.Gender;
import com.divjazz.recommendic.user.enums.MedicalCategory;
import com.divjazz.recommendic.user.model.userAttributes.Address;
import com.divjazz.recommendic.user.model.userAttributes.Role;
import com.divjazz.recommendic.user.model.userAttributes.UserName;
import com.divjazz.recommendic.user.model.userAttributes.credential.UserCredential;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;

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


    protected Patient(){}

    public Patient(
            UserName userName,
            String email,
            String phoneNumber,
            Gender gender,
            Address address, Role role, UserCredential userCredential){

        super(userName,email,phoneNumber,gender,address, role, userCredential);
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



}


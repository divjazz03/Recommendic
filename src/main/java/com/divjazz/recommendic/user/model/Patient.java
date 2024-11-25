package com.divjazz.recommendic.user.model;

import com.divjazz.recommendic.consultation.model.Consultation;
import com.divjazz.recommendic.user.enums.Gender;
import com.divjazz.recommendic.user.enums.MedicalCategory;
import com.divjazz.recommendic.user.enums.UserType;
import com.divjazz.recommendic.user.model.userAttributes.Address;
import com.divjazz.recommendic.user.model.userAttributes.Role;
import com.divjazz.recommendic.user.model.userAttributes.UserName;
import com.divjazz.recommendic.user.model.userAttributes.credential.UserCredential;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity

@Table(name = "patient")
public final class Patient extends User {

    @Enumerated(value = EnumType.STRING)
    @Column(name = "medical_categories")
    private Set<MedicalCategory> medicalCategories;

    @OneToMany(mappedBy = "patient")
    private List<Consultation> consultations;



    protected Patient(){}

    public Patient(
            UserName userName,
            String email,
            String phoneNumber,
            Gender gender,
            Address address, Role role, UserCredential userCredential){

        super(userName,email,phoneNumber,gender,address, role, userCredential);
        super.setUserType(UserType.PATIENT);
        medicalCategories = new HashSet<>(30);
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

    public List<Consultation> getConsultations() {
        return consultations;
    }

    public void setConsultations(List<Consultation> consultations) {
        this.consultations = consultations;
    }
    @Override
    public String toString() {
        return "Patient{" +
                '}';
    }



}


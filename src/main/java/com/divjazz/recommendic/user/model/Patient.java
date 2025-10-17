package com.divjazz.recommendic.user.model;

import com.divjazz.recommendic.consultation.model.ConsultationPatientData;
import com.divjazz.recommendic.user.enums.Gender;
import com.divjazz.recommendic.user.enums.UserType;
import com.divjazz.recommendic.user.model.userAttributes.PatientProfile;
import com.divjazz.recommendic.user.model.userAttributes.Role;
import com.divjazz.recommendic.user.model.userAttributes.credential.UserCredential;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "patient")
@Getter
@Setter
public class Patient extends User {
    @ManyToMany
    @JoinTable(name = "patient_medical_category",
            joinColumns = @JoinColumn(name = "patient_id"),
            inverseJoinColumns = @JoinColumn(name = "medical_category_id")
    )
    private Set<MedicalCategoryEntity> medicalCategories;

    @OneToOne(mappedBy = "patient", cascade = {CascadeType.REMOVE, CascadeType.PERSIST}, orphanRemoval = true)
    @JsonManagedReference
    private PatientProfile patientProfile;

    @OneToOne(mappedBy = "patient", cascade = {CascadeType.REMOVE}, orphanRemoval = true)
    @JsonManagedReference
    private ConsultationPatientData patientData;

    protected Patient() {
    }

    public Patient(
            String email,
            Gender gender, UserCredential userCredential, Role role) {

        super(email, gender, role, userCredential, UserType.PATIENT);
        medicalCategories = new HashSet<>();
    }
    @Override
    public String toString() {
        return "Patient{" + "targetId="+super.getUserId() + '}';
    }

    public void addMedicalCategory(MedicalCategoryEntity medicalCategoryEntity) {
        if (Objects.nonNull(medicalCategoryEntity)) {
            medicalCategories.add(medicalCategoryEntity);
        }
    }
    public void addMedicalCategories(Set<MedicalCategoryEntity> medicalCategoryEntities) {
        medicalCategories.addAll(medicalCategoryEntities);
    }
}


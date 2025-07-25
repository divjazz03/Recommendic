package com.divjazz.recommendic.user.model;

import com.divjazz.recommendic.user.enums.Gender;
import com.divjazz.recommendic.user.enums.UserType;
import com.divjazz.recommendic.user.model.userAttributes.PatientProfile;
import com.divjazz.recommendic.user.model.userAttributes.Role;
import com.divjazz.recommendic.user.model.userAttributes.credential.UserCredential;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.ArrayUtils;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Set;

@Entity
@Table(name = "patient")
@Getter
@Setter
public class Patient extends User {
    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "medical_categories", columnDefinition = "text[]")
    private String[] medicalCategories;

    @OneToOne(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private PatientProfile patientProfile;

    protected Patient() {
    }

    public Patient(
            String email,
            Gender gender, UserCredential userCredential) {

        super(email, gender, Role.PATIENT, userCredential, UserType.PATIENT);
        medicalCategories = new String[0];
    }


    public Set<String> getMedicalCategories() {
        return Set.of(this.medicalCategories);
    }

    public void setMedicalCategories(String[] medicalCategories) {

        if (this.medicalCategories == null){
            this.medicalCategories = medicalCategories;
            return;
        }
        Arrays.sort(medicalCategories);
        Arrays.sort(this.medicalCategories);

        if(Arrays.compare(medicalCategories,this.medicalCategories) != 0) {
            this.medicalCategories = ArrayUtils.addAll(this.medicalCategories, medicalCategories);
        }
    }
    @Override
    public String toString() {
        return "Patient{" + "targetId="+super.getUserId() + '}';
    }


}


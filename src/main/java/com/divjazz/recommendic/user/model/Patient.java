package com.divjazz.recommendic.user.model;

import com.divjazz.recommendic.consultation.model.Consultation;
import com.divjazz.recommendic.user.domain.MedicalCategory;
import com.divjazz.recommendic.user.enums.Gender;
import com.divjazz.recommendic.user.enums.UserType;
import com.divjazz.recommendic.user.model.userAttributes.Address;
import com.divjazz.recommendic.user.model.userAttributes.Role;
import com.divjazz.recommendic.user.model.userAttributes.UserName;
import com.divjazz.recommendic.user.model.userAttributes.credential.UserCredential;
import io.hypersistence.utils.hibernate.type.array.StringArrayType;
import jakarta.persistence.*;
import org.apache.commons.lang3.ArrayUtils;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.type.SqlTypes;

import java.util.*;

@Entity
@DiscriminatorValue("Patient")
public class Patient extends User {

    @Column(name = "medical_categories", columnDefinition = "jsonb")
    @Type(StringArrayType.class)
    @JdbcTypeCode(SqlTypes.JSON)
    private String[] medicalCategories;

    @OneToMany(mappedBy = "patient", fetch = FetchType.LAZY)
    private List<Consultation> consultations;


    protected Patient() {
    }

    public Patient(
            UserName userName,
            String email,
            String phoneNumber,
            Gender gender,
            Address address, Role role, UserCredential userCredential) {

        super(userName, email, phoneNumber, gender, address, role, userCredential);
        super.setUserType(UserType.PATIENT);;
    }


    public Set<String> getMedicalCategories() {
        return Set.of(this.medicalCategories);
    }

    public void setMedicalCategories(String[] medicalCategories) {

        if (this.medicalCategories.length == 0){
            this.medicalCategories = medicalCategories;
            return;
        }
        Arrays.sort(medicalCategories);
        Arrays.sort(this.medicalCategories);

        if(Arrays.compare(medicalCategories,this.medicalCategories) != 0) {
            this.medicalCategories = ArrayUtils.addAll(this.medicalCategories, medicalCategories);
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
        return "Patient{" + "userId="+super.getUserId() + '}';
    }


}


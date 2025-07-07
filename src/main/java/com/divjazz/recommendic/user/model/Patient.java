package com.divjazz.recommendic.user.model;

import com.divjazz.recommendic.user.enums.Gender;
import com.divjazz.recommendic.user.enums.UserType;
import com.divjazz.recommendic.user.model.userAttributes.Address;
import com.divjazz.recommendic.user.model.userAttributes.Role;
import com.divjazz.recommendic.user.model.userAttributes.UserName;
import com.divjazz.recommendic.user.model.userAttributes.credential.UserCredential;
import io.hypersistence.utils.hibernate.type.array.StringArrayType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.ArrayUtils;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.type.SqlTypes;

import java.util.Arrays;
import java.util.Set;

@Entity
@Table(name = "patient", schema = "patient_schema")
@Getter
@Setter
public class Patient extends User {

    @Column(name = "medical_categories", columnDefinition = "jsonb")
    @Type(StringArrayType.class)
    @JdbcTypeCode(SqlTypes.JSON)
    private String[] medicalCategories;
    protected Patient() {
    }

    public Patient(
            UserName userName,
            String email,
            String phoneNumber,
            Gender gender,
            Address address, UserCredential userCredential) {

        super(userName, email, phoneNumber, gender, address, Role.PATIENT, userCredential);
        super.setUserType(UserType.PATIENT);
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
        return "Patient{" + "userId="+super.getUserId() + '}';
    }


}


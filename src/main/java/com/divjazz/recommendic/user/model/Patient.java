package com.divjazz.recommendic.user.model;

import com.divjazz.recommendic.consultation.model.ConsultationPatientData;
import com.divjazz.recommendic.user.model.userAttributes.preferences.PatientNotificationPreference;
import com.divjazz.recommendic.user.enums.Gender;
import com.divjazz.recommendic.user.enums.UserType;
import com.divjazz.recommendic.user.model.userAttributes.PatientProfile;
import com.divjazz.recommendic.user.model.userAttributes.Role;
import com.divjazz.recommendic.user.model.userAttributes.credential.UserCredential;
import com.divjazz.recommendic.user.model.userAttributes.preferences.UserSecuritySetting;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.github.f4b6a3.ulid.UlidCreator;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "patients")
@Getter
@Setter
public class Patient extends User {
    @ManyToMany
    @JoinTable(name = "patients_medical_categories",
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

    @Column(name = "notification_preference", columnDefinition = "jsonb")
    @Type(JsonBinaryType.class)
    private PatientNotificationPreference notificationPreference;
    @Column(name = "security_settings", columnDefinition = "jsonb")
    @Type(JsonBinaryType.class)
    private UserSecuritySetting patientSecuritySetting;

    protected Patient() {
    }

    public Patient(
            String email,
            Gender gender, UserCredential userCredential, Role role) {

        super(email, gender, role, userCredential, UserType.PATIENT, "PT-" + UlidCreator.getMonotonicUlid());
        medicalCategories = new HashSet<>();
    }

    @Override
    public String toString() {
        return "Patient{" + "targetId=" + super.getUserId() + '}';
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


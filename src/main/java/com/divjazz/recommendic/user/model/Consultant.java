package com.divjazz.recommendic.user.model;

import com.divjazz.recommendic.consultation.model.Consultation;
import com.divjazz.recommendic.user.domain.MedicalCategory;
import com.divjazz.recommendic.user.enums.Gender;
import com.divjazz.recommendic.user.enums.MedicalCategoryEnum;
import com.divjazz.recommendic.user.enums.UserType;
import com.divjazz.recommendic.user.model.certification.Certification;
import com.divjazz.recommendic.user.model.userAttributes.Address;
import com.divjazz.recommendic.user.model.userAttributes.Role;
import com.divjazz.recommendic.user.model.userAttributes.UserName;
import com.divjazz.recommendic.user.model.userAttributes.credential.UserCredential;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import org.hibernate.annotations.Type;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@DiscriminatorValue("Consultant")
public class Consultant extends User implements Serializable {

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL,
            mappedBy = "ownerOfCertification", orphanRemoval = true)
    private Set<Certification> certificates;


    @OneToMany(fetch = FetchType.LAZY, mappedBy = "consultant")
    private List<Consultation> consultations;
    @Column(columnDefinition = "text")
    private String bio;

    @Column(name = "specialization", columnDefinition = "jsonb")
    @Type(JsonType.class)
    private MedicalCategory medicalCategory;
    private boolean certified;
    protected Consultant() {
    }

    public Consultant(
            UserName userName,
            String email,
            String phoneNumber,
            Gender gender,
            Address address,
            MedicalCategoryEnum medicalCategoryEnum, Role role, UserCredential userCredential) {
        super(userName, email, phoneNumber, gender, address, role, userCredential);
        super.setUserType(UserType.CONSULTANT);
        this.medicalCategory = new MedicalCategory(medicalCategoryEnum.getValue(), medicalCategoryEnum.getDescription());
        certificates = new HashSet<>(30);
        consultations = new ArrayList<>(20);
        certified = false;
    }

    public List<Consultation> getConsultations() {
        return consultations;
    }

    public void setConsultations(List<Consultation> consultations) {
        this.consultations = consultations;
    }

    public boolean isCertified() {
        return certified;
    }

    public void setCertified(boolean certified) {
        this.certified = certified;
    }

    /**
     * Checks if both the resume attached to the consultant has been confirmed
     */
    private void setCertified() {
        if (certificates.stream().allMatch(Certification::isConfirmed)) {
            certified = true;
        }
    }

    public Set<Certification> getCertificates() {
        return certificates;
    }

    public void setCertificates(Set<Certification> certificates) {
        this.certificates = certificates;
    }

    @Override
    public String toString() {
        return "Consultant{" + super.getUserId() + '}';
    }

    public MedicalCategory getMedicalCategory() {
        return medicalCategory;
    }

    public void setMedicalCategory(MedicalCategoryEnum medicalCategoryEnum) {
        this.medicalCategory = new MedicalCategory(medicalCategoryEnum.getValue(), medicalCategoryEnum.getDescription());
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}

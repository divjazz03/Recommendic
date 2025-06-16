package com.divjazz.recommendic.user.model;

import com.divjazz.recommendic.article.model.Article;
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
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.type.SqlTypes;

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

    @OneToMany(fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            orphanRemoval = true,
            mappedBy = "consultant"
    )
    private Set<Article> articles;


    @OneToMany(fetch = FetchType.LAZY, mappedBy = "consultant", orphanRemoval = true)
    private List<Consultation> consultations;
    @Column(columnDefinition = "text")
    private String bio;

    @Column(name = "specialization")
    private String medicalCategory;
    private boolean certified;
    protected Consultant() {
    }

    public Consultant(
            UserName userName,
            String email,
            String phoneNumber,
            Gender gender,
            Address address, Role role, UserCredential userCredential) {
        super(userName, email, phoneNumber, gender, address, role, userCredential);
        super.setUserType(UserType.CONSULTANT);
        this.medicalCategory = null;
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
        var categoryEnum =  MedicalCategoryEnum.fromValue(medicalCategory);
        return new MedicalCategory(categoryEnum.getValue(),categoryEnum.getDescription());
    }

    public void setMedicalCategory(MedicalCategoryEnum medicalCategoryEnum) {
        this.medicalCategory = medicalCategoryEnum.getValue();
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}

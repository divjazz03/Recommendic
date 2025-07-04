package com.divjazz.recommendic.user.model;

import com.divjazz.recommendic.article.model.Article;
import com.divjazz.recommendic.consultation.model.Consultation;
import com.divjazz.recommendic.user.domain.MedicalCategory;
import com.divjazz.recommendic.user.enums.Gender;
import com.divjazz.recommendic.user.enums.MedicalCategoryEnum;
import com.divjazz.recommendic.user.enums.UserType;
import com.divjazz.recommendic.user.model.certification.Certification;
import com.divjazz.recommendic.user.model.userAttributes.Address;
import com.divjazz.recommendic.user.model.userAttributes.ConsultantStat;
import com.divjazz.recommendic.user.model.userAttributes.Role;
import com.divjazz.recommendic.user.model.userAttributes.UserName;
import com.divjazz.recommendic.user.model.userAttributes.credential.UserCredential;
import io.hypersistence.utils.hibernate.type.array.StringArrayType;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.type.SqlTypes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
public class Consultant extends User{

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL,
            mappedBy = "ownerOfCertification", orphanRemoval = true)
    private Set<Certification> certificates;

    @OneToMany(fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            orphanRemoval = true,
            mappedBy = "consultant"
    )
    private Set<Article> articles;

    @Column(columnDefinition = "text")
    private String bio;

    @Column(name = "specialization")
    private String medicalCategory;
    @Column(name = "certified")
    private boolean certified;
    @Column(name = "location")
    private String locationOfInstitution;

    @Column(name = "experience")
    private int yearsOfExperience;

    @Column(name = "title")
    private String title;

    @Column(name = "languages")
    @JdbcTypeCode(SqlTypes.ARRAY)
    @Type(StringArrayType.class)
    private String[] languages;

    @OneToOne(mappedBy = "consultant",cascade = CascadeType.REMOVE)
    private ConsultantStat consultantStat;
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
        certified = false;
    }
    /**
     * Checks if both the resume attached to the consultant has been confirmed
     */
    public void setCertified() {
        if (certificates.stream().allMatch(Certification::isConfirmed)) {
            certified = true;
        }
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

}

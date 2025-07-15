package com.divjazz.recommendic.user.model;

import com.divjazz.recommendic.article.model.Article;
import com.divjazz.recommendic.user.domain.MedicalCategory;
import com.divjazz.recommendic.user.enums.Gender;
import com.divjazz.recommendic.user.enums.MedicalCategoryEnum;
import com.divjazz.recommendic.user.enums.UserType;
import com.divjazz.recommendic.user.model.certification.Certification;
import com.divjazz.recommendic.user.model.userAttributes.*;
import com.divjazz.recommendic.user.model.userAttributes.credential.UserCredential;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import io.hypersistence.utils.hibernate.type.array.StringArrayType;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.type.SqlTypes;

import java.util.HashSet;
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


    @Column(name = "specialization")
    private String medicalCategory;

    @Column(name = "certified")
    private boolean certified;

    @OneToOne(mappedBy = "consultant", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private ConsultantProfile profile;


    protected Consultant() {
    }

    public Consultant(
            String email,
            Gender gender,UserCredential userCredential) {
        super( email, gender, Role.CONSULTANT, userCredential, UserType.CONSULTANT);
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

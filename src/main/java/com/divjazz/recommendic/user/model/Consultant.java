package com.divjazz.recommendic.user.model;

import com.divjazz.recommendic.article.model.Article;
import com.divjazz.recommendic.user.enums.Gender;
import com.divjazz.recommendic.user.enums.UserType;
import com.divjazz.recommendic.user.model.certification.Certification;
import com.divjazz.recommendic.user.model.certification.ConsultantEducation;
import com.divjazz.recommendic.user.model.userAttributes.ConsultantProfile;
import com.divjazz.recommendic.user.model.userAttributes.Role;
import com.divjazz.recommendic.user.model.userAttributes.credential.UserCredential;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
public class Consultant extends User{

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST,
            mappedBy = "ownerOfCertification", orphanRemoval = true)
    @JsonIgnore
    private Set<Certification> certificates;

    @OneToMany(fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            orphanRemoval = true,
            mappedBy = "consultant"
    )
    @JsonIgnore
    private Set<Article> articles;


    @JoinColumn(name = "specialization")
    @OneToOne
    private MedicalCategoryEntity specialization;

    @Column(name = "certified")
    private boolean certified;

    @OneToOne(mappedBy = "consultant", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    @JsonManagedReference
    private ConsultantProfile profile;

    @OneToMany(mappedBy = "consultant")
    private Set<ConsultantEducation> consultantEducation;


    protected Consultant() {
    }

    public Consultant(
            String email,
            Gender gender, UserCredential userCredential, Role role) {
        super( email, gender, role, userCredential, UserType.CONSULTANT);
        specialization = null;
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


}

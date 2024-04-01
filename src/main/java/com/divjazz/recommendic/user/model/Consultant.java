package com.divjazz.recommendic.user.model;

import com.divjazz.recommendic.user.enums.MedicalCategory;
import com.divjazz.recommendic.user.model.certification.Certification;
import com.divjazz.recommendic.user.model.userAttributes.*;
import io.github.wimdeblauwe.jpearl.AbstractEntity;
import jakarta.persistence.*;
import org.aspectj.weaver.patterns.IfPointcut;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
public class Consultant extends AbstractEntity<UserId> {

    @OneToMany(cascade = CascadeType.ALL)
    private Set<Certification> certifications;

    @ManyToMany
    private Set<Patient> patients;
    @OneToOne(targetEntity = User.class, optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "tt_user_id", nullable = false)
    private User user;
    @Enumerated(value = EnumType.STRING)
    private MedicalCategory medicalCategory;

    private boolean certified;
    protected Consultant (){}

    public Consultant (UserId id, User user, MedicalCategory medicalCategory){
        super(id);
        this.user = user;
        this.medicalCategory = medicalCategory;
        certifications = new HashSet<>();
    }
    public Consultant (UserId id, User user, Certification certification){
        super(id);
        this.user = user;

        certifications.add(Objects.requireNonNull(certification, "certification was null"));
    }
    public boolean isCertified() {
        return certified;
    }

    /**
     * Checks if both the resume attached to the consultant has been confirmed
     */
    private void setCertified(){
        if (certifications.stream().allMatch(Certification::isConfirmed)){
            certified = true;
        }
    }

    public Set<Certification> getCertifications() {
        return certifications;
    }

    public User getUser() {
        return user;
    }

    public Set<Patient> getPatients() {
        return patients;
    }

    public void setPatients(Set<Patient> patients) {
        this.patients.addAll(patients);
    }

    @Override
    public String toString() {
        return "Consultant: name -> " + user.getUserNameObject().getFullName() +
                "email -> " + user.getEmail() +
                "gender -> " + user.getGender().name();
    }

    public MedicalCategory getMedicalCategory() {
        return medicalCategory;
    }
}

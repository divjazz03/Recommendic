package com.divjazz.recommendic.user.model;

import com.divjazz.recommendic.user.model.certification.Certification;
import com.divjazz.recommendic.user.model.userAttributes.*;
import io.github.wimdeblauwe.jpearl.AbstractEntity;
import jakarta.persistence.*;

import java.util.Set;

@Entity
public class Consultant extends AbstractEntity<UserId> {

    @OneToMany
    private Set<Certification> certifications;

    @ManyToMany
    private Set<Patient> patients;
    @OneToOne(targetEntity = User.class, optional = false)
    @JoinColumn(name = "tt_user_id", nullable = false)
    private User user;

    private boolean certified;
    protected Consultant (){}

    public Consultant (UserId id, User user){
        super(id);
        this.user = user;
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

}

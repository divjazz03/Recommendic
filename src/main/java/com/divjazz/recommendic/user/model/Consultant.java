package com.divjazz.recommendic.user.model;

import com.divjazz.recommendic.user.UserType;
import com.divjazz.recommendic.user.model.certification.CertificationFromUni;
import com.divjazz.recommendic.user.model.certification.CertificationID;
import com.divjazz.recommendic.user.model.certification.Resume;
import com.divjazz.recommendic.user.model.userAttributes.*;
import io.github.wimdeblauwe.jpearl.AbstractEntity;
import jakarta.persistence.*;
import org.hibernate.annotations.Cascade;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

@Entity
public class Consultant extends AbstractEntity<UserId> {


    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id")
    private Resume resume;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "certification_id")
    private CertificationFromUni certification;

    @ManyToMany
    private Set<Patient> patients;
    @OneToOne(targetEntity = User.class, optional = false)
    @JoinColumn(name = "tt_user_id", nullable = false)
    private User user;

    private boolean certified;
    protected Consultant (){}

    public Consultant (UserId id){
        super(id);
    }
    public boolean isCertified() {
        return certified;
    }

    /**
     * Checks if both the resume attached to the consultant has been confirmed
     */
    private void setCertified(){
        if (resume.isConfirmed() && certification.isConfirmed()){
            certified = true;
        }
    }

    public Resume getResume() {
        return resume;
    }

    public void setResume(Resume resume) {
        this.resume = resume;
    }

    public CertificationFromUni getCertification() {
        return certification;
    }

    public void setCertification(CertificationFromUni certification) {
        this.certification = certification;
    }

    public Set<Patient> getPatients() {
        return patients;
    }

    public void setPatients(Set<Patient> patients) {
        this.patients.addAll(patients);
    }
}

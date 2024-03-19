package com.divjazz.recommendic.user.model;

import com.divjazz.recommendic.user.UserType;
import com.divjazz.recommendic.user.model.certification.CertificationFromUni;
import com.divjazz.recommendic.user.model.certification.Resume;
import com.divjazz.recommendic.user.model.userAttributes.*;
import jakarta.persistence.*;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;


public class Consultant {


    private Set<Resume> resume;


    private Set<CertificationFromUni> certification;


    private Set<Patient> patients;


    private String password;

    private boolean certified;
    protected Consultant (){}


    public boolean isCertified() {
        return certified;
    }

    /**
     * Checks if both the resume attached to the consultant has been confirmed
     */
    private void setCertified(){
        boolean checkIfExists = this.resume.stream().findFirst().isPresent() && this.certification.stream().findFirst().isPresent();
        boolean resumeIsVerified = this.resume.stream()
                .findFirst()
                .get()
                .isConfirmed();
        boolean certificateIsVerified = this.resume.stream()
                .findFirst()
                .get()
                .isConfirmed();
        certified = (checkIfExists && resumeIsVerified && certificateIsVerified);

    }

    public Set<Resume> getResume() {
        return resume;
    }

    public void setResume(Resume resume) {
        this.resume = Collections.singleton(resume);
    }

    public Set<CertificationFromUni> getCertification() {
        return certification;
    }

    public void setCertification(CertificationFromUni certification) {
        this.certification = Collections.singleton(certification);
    }

    public Set<Patient> getPatients() {
        return patients;
    }

    public void setPatients(Set<Patient> patients) {
        this.patients.addAll(patients);
    }
}

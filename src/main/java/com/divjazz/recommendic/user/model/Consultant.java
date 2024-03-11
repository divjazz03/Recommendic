package com.divjazz.recommendic.user.model;

import com.divjazz.recommendic.user.model.certification.CertificationFromUni;
import com.divjazz.recommendic.user.model.certification.Resume;
import com.divjazz.recommendic.user.model.userAttributes.*;
import jakarta.persistence.*;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

@Entity
public class Consultant extends User implements UserDetails {

    @OneToMany(fetch = FetchType.EAGER)
    @Cascade(CascadeType.ALL)
    @JoinColumn(name = "resume_id")
    private Set<Resume> resume;

    @OneToMany(fetch = FetchType.EAGER)
    @Cascade(CascadeType.ALL)
    @JoinColumn(name = "certificate_id")
    private Set<CertificationFromUni> certification;

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Patient> patients;

    @Column(name = "password", nullable = false)
    private String password;

    private boolean certified;
    protected Consultant (){}
    public Consultant(UserId id,
                      UserName userName,
                      Email email,
                      PhoneNumber phoneNumber,
                      Gender gender,
                      Address address,
                      String password) {
        super(id, userName, email, phoneNumber, gender,address);
        this.password = password;
    }


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

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return getEmail().asString();
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}

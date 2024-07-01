package com.divjazz.recommendic.user.model;

import com.divjazz.recommendic.search.Search;
import com.divjazz.recommendic.user.enums.Gender;
import com.divjazz.recommendic.user.enums.MedicalCategory;
import com.divjazz.recommendic.user.model.certification.Certification;
import com.divjazz.recommendic.user.model.userAttributes.*;
import io.github.wimdeblauwe.jpearl.AbstractEntity;
import jakarta.persistence.*;
import org.aspectj.weaver.patterns.IfPointcut;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.*;

@Entity
public final class Consultant extends User{

    @OneToMany(cascade = CascadeType.ALL)
    private Set<Certification> certifications;

    @ManyToMany
    private Set<Patient> patients;

    @Enumerated(value = EnumType.STRING)
    private MedicalCategory medicalCategory;

    private boolean certified;
    protected Consultant (){}

    public Consultant (UUID id,
                       UserName userName,
                       String email,
                       String phoneNumber,
                       Gender gender,
                       Address address,
                       String password,
                       MedicalCategory medicalCategory){
        super(id,userName,email,phoneNumber,gender,address,password);
        this.medicalCategory = medicalCategory;
        certifications = new HashSet<>(30);
        patients = new HashSet<>(30);
        certified = false;
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

    public Set<Patient> getPatients() {
        return patients;
    }

    public void setPatients(Set<Patient> patients) {
        this.patients.addAll(patients);
    }

    @Override
    public String toString() {
        return "Consultant{" +
                "certifications=" + certifications +
                ", patients=" + patients +
                ", medicalCategory=" + medicalCategory +
                ", certified=" + certified +
                '}';
    }

    public MedicalCategory getMedicalCategory() {
        return medicalCategory;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("CONSULTANT"));
    }
}

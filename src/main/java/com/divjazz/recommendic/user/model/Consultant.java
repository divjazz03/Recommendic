package com.divjazz.recommendic.user.model;

import com.divjazz.recommendic.recommendation.model.Recommendation;
import com.divjazz.recommendic.user.enums.Gender;
import com.divjazz.recommendic.user.enums.MedicalCategory;
import com.divjazz.recommendic.user.model.certification.Certification;
import com.divjazz.recommendic.user.model.userAttributes.*;

import com.divjazz.recommendic.user.model.userAttributes.credential.ConsultantCredential;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.*;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_DEFAULT;

@Entity
@Table(name = "consultant")
@JsonInclude(NON_DEFAULT)
public final class Consultant extends User{

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "ownerOfCertification")
    private Set<Certification> certifications;


    @ManyToMany
    @JoinTable(
            name = "consultant_patient",
            joinColumns = { @JoinColumn(name = "consultant_id") },
            inverseJoinColumns = { @JoinColumn(name = "patient_id") }
    )
    private Set<Patient> patients;

    @Column(columnDefinition = "text")
    private String bio;
    @OneToMany
    private Set<Recommendation> recommendation;
    @Enumerated(value = EnumType.STRING)
    private MedicalCategory medicalCategory;

    @OneToOne
    private ConsultantCredential credential;

    private boolean certified;
    protected Consultant (){}

    public Consultant (UUID id,
                       UserName userName,
                       String email,
                       String phoneNumber,
                       Gender gender,
                       Address address,
                       MedicalCategory medicalCategory){
        super(userName,email,phoneNumber,gender,address);
        setId(id);
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
                ", medicalSpecialization=" + medicalCategory +
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

    @Override
    public String getPassword() {
        return this.credential.getPassword();
    }

    public String getBio() {
        return bio;
    }

    public Set<Recommendation> getRecommendation() {
        return recommendation;
    }

    public ConsultantCredential getCredential() {
        return credential;
    }

    public void setCredential(ConsultantCredential credential) {
        this.credential = credential;
    }

    public void setCertifications(Set<Certification> certifications) {
        this.certifications = certifications;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public void setRecommendation(Set<Recommendation> recommendation) {
        this.recommendation = recommendation;
    }

    public void setMedicalCategory(MedicalCategory medicalCategory) {
        this.medicalCategory = medicalCategory;
    }

    public void setCertified(boolean certified) {
        this.certified = certified;
    }
}

package com.divjazz.recommendic.user.model;

import com.divjazz.recommendic.user.enums.Gender;
import com.divjazz.recommendic.user.enums.MedicalCategory;
import com.divjazz.recommendic.user.model.certification.Certification;
import com.divjazz.recommendic.user.model.userAttributes.*;

import com.divjazz.recommendic.user.model.userAttributes.credential.UserCredential;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.*;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_DEFAULT;

@Entity
@Table(name = "consultant")
@JsonInclude(NON_DEFAULT)
public final class Consultant extends User implements Serializable {

    @OneToMany(fetch = FetchType.LAZY,cascade = CascadeType.ALL,
            mappedBy = "ownerOfCertification", orphanRemoval = true)
    private Set<Certification> certificates;


    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, targetEntity = Patient.class)
    @JoinTable(
            name = "consultant_patient",
            joinColumns = { @JoinColumn(name = "consultant_id") },
            inverseJoinColumns = { @JoinColumn(name = "patient_id") }
    )
    private Set<Patient> patients;

    @Column(columnDefinition = "text")
    private String bio;
    @Enumerated(value = EnumType.STRING)
    @Column(name = "specialization")
    private MedicalCategory medicalCategory;

    private boolean certified;
    protected Consultant (){}

    public Consultant (
            UserName userName,
            String email,
            String phoneNumber,
            Gender gender,
            Address address,
            MedicalCategory medicalCategory, Role role, UserCredential userCredential){
        super(userName,email,phoneNumber,gender,address, role, userCredential);
        this.medicalCategory = medicalCategory;
        certificates = new HashSet<>(30);
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
        if (certificates.stream().allMatch(Certification::isConfirmed)){
            certified = true;
        }
    }

    public Set<Certification> getCertificates() {
        return certificates;
    }

    public Set<Patient> getPatients() {
        return patients;
    }

    public void setPatients(Set<Patient> patients) {
        this.patients = patients;
    }

    public void addPatient(Patient patient) {
        this.patients.add(patient);
    }

    public void addPatients(Set<Patient> patients) {
        this.patients.addAll(patients);
    }
    public void removePatient(Patient patient) {
        this.patients.remove(patient);
    }
    public void removePatients(Set<Patient> patients) {
        this.patients.removeAll(patients);
    }

    @Override
    public String toString() {
        return "Consultant{" +
                "certificates=" + certificates +
                ", patients=" + patients +
                ", medicalSpecialization=" + medicalCategory +
                ", certified=" + certified +
                '}';
    }

    public MedicalCategory getMedicalCategory() {
        return medicalCategory;
    }


    public String getBio() {
        return bio;
    }




    public void setCertificates(Set<Certification> certificates) {
        this.certificates = certificates;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }


    public void setMedicalCategory(MedicalCategory medicalCategory) {
        this.medicalCategory = medicalCategory;
    }

    public void setCertified(boolean certified) {
        this.certified = certified;
    }
}

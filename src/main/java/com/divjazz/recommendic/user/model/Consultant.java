package com.divjazz.recommendic.user.model;

import com.divjazz.recommendic.user.model.certification.CertificationFromUni;
import com.divjazz.recommendic.user.model.certification.Resume;
import com.divjazz.recommendic.user.model.userAttributes.*;
import jakarta.persistence.*;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import java.util.Set;

@Entity
public class Consultant extends User{

    @OneToOne(fetch = FetchType.EAGER)
    @Cascade(CascadeType.ALL)
    @JoinColumn(name = "resume_id")
    private Resume resume;

    @OneToOne(fetch = FetchType.EAGER)
    @Cascade(CascadeType.ALL)
    @JoinColumn(name = "certificate_id")
    private CertificationFromUni certification;

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Patient> patients;

    private boolean certified;
    protected Consultant (){}
    public Consultant(UserId id,
                      UserName userName,
                      Email email,
                      PhoneNumber phoneNumber,
                      Gender gender,
                      Resume resume,
                      CertificationFromUni uni) {
        super(id, userName, email, phoneNumber, gender);
    }


    public boolean isCertified() {
        return certified;
    }

    /**
     * Checks if both the resume attached to the consultant has been confirmed
     */
    private void setCertified(){
        certified = this.resume.isConfirmed() && this.certification.isConfirmed();
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

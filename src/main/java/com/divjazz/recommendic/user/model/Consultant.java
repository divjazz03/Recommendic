package com.divjazz.recommendic.user.model;

import com.divjazz.recommendic.user.model.certification.CertificationFromUni;
import com.divjazz.recommendic.user.model.certification.Resume;
import com.divjazz.recommendic.user.model.userAttributes.*;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import java.util.Set;

@Entity
public class Consultant extends User{

    @OneToOne
    @Cascade(CascadeType.ALL)
    private Resume resume;

    @OneToOne
    @Cascade(CascadeType.ALL)
    private CertificationFromUni certification;

    @ManyToMany
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
}

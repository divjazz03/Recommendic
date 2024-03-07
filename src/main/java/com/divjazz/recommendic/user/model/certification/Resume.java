package com.divjazz.recommendic.user.model.certification;

import com.divjazz.recommendic.user.model.Consultant;
import io.github.wimdeblauwe.jpearl.AbstractEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import java.io.File;

@Entity
public class Resume extends AbstractEntity<CertificationID> {


    @OneToOne
    private Consultant ownerOFTheResume;
    private File pdfOfResume;

    private boolean confirmed = false;

    protected Resume(){}
    public Resume(CertificationID id, Consultant ownerOFTheResume, File pdfOfResume) {
        super(id);
        this.ownerOFTheResume = ownerOFTheResume;
        this.pdfOfResume = pdfOfResume;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    public boolean isConfirmed() {
        return confirmed;
    }
}

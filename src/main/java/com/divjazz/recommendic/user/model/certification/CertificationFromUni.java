package com.divjazz.recommendic.user.model.certification;

import com.divjazz.recommendic.user.model.Consultant;
import io.github.wimdeblauwe.jpearl.AbstractEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;

import java.io.File;

@Entity
public class CertificationFromUni extends AbstractEntity<CertificationID> {

    @OneToOne
    private Consultant ownerOfCertification;
    private File certificationInPdfFormat;

    private boolean confirmed;
    protected CertificationFromUni(){}

    public CertificationFromUni(CertificationID id, Consultant ownerOfCertification, File certificationInPdfFormat) {
        super(id);
        this.ownerOfCertification = ownerOfCertification;
        this.certificationInPdfFormat = certificationInPdfFormat;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }
}

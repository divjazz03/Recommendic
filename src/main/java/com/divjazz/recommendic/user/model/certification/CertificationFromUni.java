package com.divjazz.recommendic.user.model.certification;

import com.divjazz.recommendic.user.model.Consultant;
import io.github.wimdeblauwe.jpearl.AbstractEntity;
import jakarta.persistence.*;

import java.io.File;

@Entity
public class CertificationFromUni extends AbstractEntity<CertificationID>{

    @OneToOne(targetEntity = Consultant.class, optional = false)
    @JoinColumn(name = "consultant_id", nullable = false)
    private Consultant ownerOfCertification;

    @Lob
    private Byte[] certificationInPdfFormat;

    private boolean confirmed;
    protected CertificationFromUni(){}

    public CertificationFromUni(CertificationID id, Consultant ownerOfCertification, Byte[] certificationInPdfFormat) {
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

    public Consultant getOwnerOfCertification() {
        return ownerOfCertification;
    }

    public void setOwnerOfCertification(Consultant ownerOfCertification) {
        this.ownerOfCertification = ownerOfCertification;
    }

    public Byte[] getCertificationInPdfFormat() {
        return certificationInPdfFormat;
    }

    public void setCertificationInPdfFormat(Byte[] certificationInPdfFormat) {
        this.certificationInPdfFormat = certificationInPdfFormat;
    }
}

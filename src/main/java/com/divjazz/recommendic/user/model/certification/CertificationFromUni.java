package com.divjazz.recommendic.user.model.certification;

import com.divjazz.recommendic.user.model.Consultant;
import io.github.wimdeblauwe.jpearl.AbstractEntity;
import jakarta.persistence.*;

import java.io.File;


public class CertificationFromUni{


    private Consultant ownerOfCertification;

    @Lob
    private Byte[] certificationInPdfFormat;

    private boolean confirmed;
    protected CertificationFromUni(){}

    public CertificationFromUni(CertificationID id, Consultant ownerOfCertification, Byte[] certificationInPdfFormat) {

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

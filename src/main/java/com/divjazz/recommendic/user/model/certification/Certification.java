package com.divjazz.recommendic.user.model.certification;

import com.divjazz.recommendic.user.model.Consultant;
import io.github.wimdeblauwe.jpearl.AbstractEntity;
import jakarta.persistence.*;

@Entity
public class Certification extends AbstractEntity<CertificationID> {

    @OneToOne(targetEntity = Consultant.class,
            cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "consultant_id", nullable = false)
    private Consultant ownerOfCertification;

    private String fileName;

    private CertificateType certificateType;

    @Lob
    private Byte[] fileContent;

    private boolean confirmed = false;

    protected Certification(){}

    public Certification(CertificationID id, Consultant ownerOfCertification, String fileName){
        super(id);
        this.ownerOfCertification = ownerOfCertification;
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    public CertificateType getCertificateType() {
        return certificateType;
    }

    public Byte[] getFileContent() {
        return fileContent;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public Consultant getOwnerOfCertification() {
        return ownerOfCertification;
    }

}

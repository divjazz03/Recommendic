package com.divjazz.recommendic.user.model.certification;

import com.divjazz.recommendic.user.enums.CertificateType;
import com.divjazz.recommendic.user.model.Consultant;
import io.github.wimdeblauwe.jpearl.AbstractEntity;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
public class Certification {
    @Id
    private UUID id;
    @OneToOne(cascade = CascadeType.ALL)
    private Consultant ownerOfCertification;

    private String fileName;
    private String fileUrl;

    private CertificateType certificateType;


    private boolean confirmed = false;

    protected Certification(){}

    public Certification(UUID id, Consultant ownerOfCertification, String fileName, String fileUrl){
        this.id = id;
        this.ownerOfCertification = ownerOfCertification;
        this.fileName = fileName;
        this.fileUrl = fileUrl;
    }


    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Consultant getOwnerOfCertification() {
        return ownerOfCertification;
    }

    public void setOwnerOfCertification(Consultant ownerOfCertification) {
        this.ownerOfCertification = ownerOfCertification;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public CertificateType getCertificateType() {
        return certificateType;
    }

    public void setCertificateType(CertificateType certificateType) {
        this.certificateType = certificateType;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }
}

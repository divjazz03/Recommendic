package com.divjazz.recommendic.user.model.certification;

import com.divjazz.recommendic.Auditable;
import com.divjazz.recommendic.user.enums.CertificateType;
import com.divjazz.recommendic.user.model.Consultant;
import com.divjazz.recommendic.user.model.User;
import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "certification")
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class Certification extends Auditable implements Serializable{
    @ManyToOne(targetEntity = Consultant.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "consultant_id", nullable = false)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    @JsonProperty("user_id")
    private User ownerOfCertification;

    @ManyToOne( fetch = FetchType.LAZY)
    @JoinColumn(name = "assignment_id")
    private Assignment assignment;

    @Column(name = "file_name")
    private String fileName;
    @Column(name = "file_url")
    private String fileUrl;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "certificate_type")
    private CertificateType certificateType;

    @Column(name = "confirmed")
    private boolean confirmed = false;

    protected Certification(){}

    public Certification(Consultant ownerOfCertification, String fileName, String fileUrl){
        super();
        this.ownerOfCertification = ownerOfCertification;
        this.fileName = fileName;
        this.fileUrl = fileUrl;
    }


    public User getOwnerOfCertification() {
        return ownerOfCertification;
    }

    public void setOwnerOfCertification(User ownerOfCertification) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Certification that = (Certification) o;

        return Objects.equals(fileName, that.fileName) &&  Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        var id = getId();
        var res =  fileName != null ? fileName.hashCode() : 0;
        return (int) (31 * res + id ^ (id >>> 31));
    }
}

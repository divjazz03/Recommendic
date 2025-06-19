package com.divjazz.recommendic.user.model.certification;

import com.divjazz.recommendic.Auditable;
import com.divjazz.recommendic.user.enums.CertificateType;
import com.divjazz.recommendic.user.model.Assignment;
import com.divjazz.recommendic.user.model.Consultant;
import com.divjazz.recommendic.user.model.User;
import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "certification")
@Getter
@Setter
public class Certification extends Auditable{
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consultant_id", nullable = false)
    private Consultant ownerOfCertification;

    @ManyToOne(fetch = FetchType.LAZY)
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

    protected Certification() {
    }

    public Certification(Consultant ownerOfCertification, String fileName, String fileUrl) {
        super();
        this.ownerOfCertification = ownerOfCertification;
        this.fileName = fileName;
        this.fileUrl = fileUrl;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Certification that = (Certification) o;

        return Objects.equals(fileName, that.fileName) && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        var id = getId();
        var res = fileName != null ? fileName.hashCode() : 0;
        return (int) (31 * res + id ^ (id >>> 31));
    }
}

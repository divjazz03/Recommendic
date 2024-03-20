package com.divjazz.recommendic.user.model.certification;

import com.divjazz.recommendic.user.model.Consultant;
import com.divjazz.recommendic.user.model.userAttributes.UserId;
import io.github.wimdeblauwe.jpearl.AbstractEntity;
import jakarta.persistence.*;
import org.hibernate.annotations.Cascade;

import java.io.File;

@Entity
public class Resume extends AbstractEntity<UserId> {
    @OneToOne(targetEntity = Consultant.class,
            cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "consultant_id", nullable = false)
    private Consultant ownerOFTheResume;
    @Lob
    private Byte[] pdfOfResume;

    private boolean confirmed = false;

    protected Resume(){}
    public Resume(UserId id, Consultant ownerOFTheResume, Byte[] pdfOfResume) {
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

    public Consultant getOwnerOFTheResume() {
        return ownerOFTheResume;
    }

    public void setOwnerOFTheResume(Consultant ownerOFTheResume) {
        this.ownerOFTheResume = ownerOFTheResume;
    }

    public Byte[] getPdfOfResume() {
        return pdfOfResume;
    }

    public void setPdfOfResume(Byte[] pdfOfResume) {
        this.pdfOfResume = pdfOfResume;
    }
}

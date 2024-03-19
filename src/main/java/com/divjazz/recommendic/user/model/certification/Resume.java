package com.divjazz.recommendic.user.model.certification;

import com.divjazz.recommendic.user.model.Consultant;
import io.github.wimdeblauwe.jpearl.AbstractEntity;
import jakarta.persistence.*;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import java.io.File;


public class Resume{



    private Consultant ownerOFTheResume;
    @Lob
    private Byte[] pdfOfResume;

    private boolean confirmed = false;

    protected Resume(){}
    public Resume(Consultant ownerOFTheResume, Byte[] pdfOfResume) {

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

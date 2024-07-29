package com.divjazz.recommendic.user.model.userAttributes.credential;

import com.divjazz.recommendic.user.model.Consultant;
import com.divjazz.recommendic.user.model.User;
import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.UUID;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_DEFAULT;

@JsonInclude(NON_DEFAULT)
@Entity
@Table(name = "consultant_credential")
public class ConsultantCredential extends UserCredential {
    @OneToOne(targetEntity = Consultant.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "consultant_id", nullable = false)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    @JsonProperty("user_id")
    private User consultant;

    public ConsultantCredential(Consultant consultant, String password) {
        super(password);
        this.consultant = consultant;
    }

    protected ConsultantCredential() {
    }

    public User getConsultant() {
        return consultant;
    }

    public void setConsultant(Consultant consultant) {
        this.consultant = consultant;
    }
}

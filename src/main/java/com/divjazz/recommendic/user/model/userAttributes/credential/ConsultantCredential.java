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
public class ConsultantCredential extends UserCredential {
    @OneToOne(targetEntity = Consultant.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "consultant_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    @JsonProperty("user_id")
    private User consultant;

    public ConsultantCredential(Consultant consultant, String password, UUID referenceId) {
        super(password, referenceId);
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

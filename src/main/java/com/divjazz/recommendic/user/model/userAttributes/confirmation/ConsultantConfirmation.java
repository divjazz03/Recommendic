package com.divjazz.recommendic.user.model.userAttributes.confirmation;

import com.divjazz.recommendic.user.model.Consultant;
import com.divjazz.recommendic.user.model.User;
import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_DEFAULT;

@Entity
@JsonInclude(NON_DEFAULT)
public class ConsultantConfirmation extends UserConfirmation{
    @OneToOne(targetEntity = Consultant.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "consultant_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    @JsonProperty("user_id")
    @NotNull
    private User consultant;

    public ConsultantConfirmation(User admin) {
        this.consultant = admin;
    }
    protected ConsultantConfirmation(){}

    public User getConsultant() {
        return consultant;
    }

    public void setConsultant(User admin) {
        this.consultant = admin;
    }
}

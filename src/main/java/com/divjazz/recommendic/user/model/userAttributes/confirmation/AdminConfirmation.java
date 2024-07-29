package com.divjazz.recommendic.user.model.userAttributes.confirmation;

import com.divjazz.recommendic.user.model.Admin;
import com.divjazz.recommendic.user.model.User;
import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.io.Serializable;

@Entity
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class AdminConfirmation extends UserConfirmation implements Serializable {
    @OneToOne(targetEntity = Admin.class, fetch = FetchType.EAGER)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    @JsonProperty("user_id")
    @NotNull
    private User admin;
    protected AdminConfirmation(){}

    public AdminConfirmation(Admin admin) {
        this.admin = admin;
    }

    public User getAdmin() {
        return admin;
    }

    public void setAdmin(User admin) {
        this.admin = admin;
    }
}

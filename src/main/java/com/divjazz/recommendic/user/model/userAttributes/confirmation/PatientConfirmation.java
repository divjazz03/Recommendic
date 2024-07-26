package com.divjazz.recommendic.user.model.userAttributes.confirmation;

import com.divjazz.recommendic.user.model.Patient;
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
public class PatientConfirmation extends UserConfirmation{
    @OneToOne(targetEntity = Patient.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "patient_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    @JsonProperty("user_id")
    @NotNull
    private User patient;

    public PatientConfirmation(Patient patient) {
        this.patient = patient;
    }
    protected PatientConfirmation(){}

    public User getAdmin() {
        return patient;
    }

    public void setAdmin(Patient patient) {
        this.patient = patient;
    }
}

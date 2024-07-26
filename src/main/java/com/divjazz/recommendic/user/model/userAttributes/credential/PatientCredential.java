package com.divjazz.recommendic.user.model.userAttributes.credential;

import com.divjazz.recommendic.user.model.Patient;
import com.divjazz.recommendic.user.model.User;
import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.UUID;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_DEFAULT;

@Entity
@JsonInclude(NON_DEFAULT)
public class PatientCredential extends UserCredential {
    @OneToOne(targetEntity = Patient.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "patient_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    @JsonProperty("user_id")
    private User patient;

    public PatientCredential(Patient patient, String password, UUID referenceId) {
        super(password, referenceId);
        this.patient = patient;
    }

    protected PatientCredential() {
    }

    public User getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }
}

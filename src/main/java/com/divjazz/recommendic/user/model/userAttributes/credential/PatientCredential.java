package com.divjazz.recommendic.user.model.userAttributes.credential;

import com.divjazz.recommendic.user.model.Patient;
import com.divjazz.recommendic.user.model.User;
import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.Objects;
import java.util.UUID;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_DEFAULT;

@Entity
@JsonInclude(NON_DEFAULT)
@Table(name = "patient_credential")
public class PatientCredential extends UserCredential {
    @OneToOne(targetEntity = Patient.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "patient_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    @JsonProperty("user_id")
    private User patient;

    public PatientCredential(Patient patient, String password) {
        super(password);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PatientCredential that = (PatientCredential) o;

        return Objects.equals(patient, that.patient) &&
                Objects.equals(getPassword(), that.getPassword());
    }

    @Override
    public int hashCode() {
        return patient != null ? patient.hashCode() : 0;
    }
}

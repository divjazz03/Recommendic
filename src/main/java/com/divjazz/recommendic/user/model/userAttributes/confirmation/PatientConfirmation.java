package com.divjazz.recommendic.user.model.userAttributes.confirmation;

import com.divjazz.recommendic.user.model.Patient;
import com.divjazz.recommendic.user.model.User;
import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_DEFAULT;

@Entity
@JsonInclude(NON_DEFAULT)
@Table(name = "patient_confirmation")
public class PatientConfirmation extends UserConfirmation{
    @ManyToOne (targetEntity = Patient.class, fetch = FetchType.EAGER)
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

    public User getPatient() {
        return patient;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PatientConfirmation that = (PatientConfirmation) o;
        return Objects.equals(getKey(), that.getKey());
    }

    @Override
    public int hashCode() {
        return getKey() != null ? getKey().hashCode() : 0;
    }
}

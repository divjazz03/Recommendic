package com.divjazz.recommendic.consultation.model;

import com.divjazz.recommendic.Auditable;
import com.divjazz.recommendic.consultation.enums.Status;
import com.divjazz.recommendic.user.model.Consultant;
import com.divjazz.recommendic.user.model.Patient;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "consultation")
public class Consultation extends Auditable {

    @Column(name = "diagnosis")
    private String diagnosis;

    @Column(name = "consultation_time")
    private LocalDateTime consultationTime;

    @ManyToOne(optional = false)
    @JoinColumn(name = "patient_id")
    private Patient patient;
    @ManyToOne(optional = false)
    @JoinColumn(name = "consultant_id")
    private Consultant consultant;

    private boolean accepted;

    @Enumerated(EnumType.STRING)
    private Status status;

    protected Consultation(){}
    public Consultation(String diagnosis, LocalDateTime consultationTime, Patient patient, Consultant consultant) {
        this.diagnosis = diagnosis;
        this.consultationTime = consultationTime;
        this.patient = patient;
        this.consultant = consultant;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public LocalDateTime getConsultationTime() {
        return consultationTime;
    }

    public void setConsultationTime(LocalDateTime consultationTime) {
        this.consultationTime = consultationTime;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Consultant getConsultant() {
        return consultant;
    }

    public void setConsultant(Consultant consultant) {
        this.consultant = consultant;
    }


    public boolean isAccepted() {
        return accepted;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Consultation that = (Consultation) o;

        return Objects.equals(consultant, that.consultant) &&
                Objects.equals(patient, that.patient) &&
                Objects.equals(consultationTime, that.consultationTime);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (consultationTime != null ? consultationTime.hashCode() : 0);
        result = 31 * result + (patient != null ? patient.hashCode() : 0);
        result = 31 * result + (consultant != null ? consultant.hashCode() : 0);
        return result;
    }
}

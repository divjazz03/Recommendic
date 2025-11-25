package com.divjazz.recommendic.consultation.model;

import com.divjazz.recommendic.consultation.enums.PatientStatus;
import com.divjazz.recommendic.global.Auditable;
import com.divjazz.recommendic.user.model.Consultant;
import com.divjazz.recommendic.user.model.Patient;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "consultation_session")
@Getter
public class ConsultationSession extends Auditable {
    @ManyToOne
    @JoinColumn(name = "patient_id")
    private Patient patient;
    @ManyToOne
    @JoinColumn(name = "consultant_id")
    private Consultant consultant;
    @OneToMany(mappedBy = "session")
    private Set<Consultation> consultations;
    @Column(name = "patient_status")
    @Enumerated(value = EnumType.STRING)
    @Setter
    private PatientStatus patientStatus;
    @Column(name = "condition")
    @Setter
    private String condition;


    protected ConsultationSession () {
        this.patient = null;
        this.consultant = null;
        this.consultations = Set.of();
    }
    public ConsultationSession(Patient patient, Consultant consultant, Consultation consultation) {
        this.patient = patient;
        this.consultant = consultant;
        this.consultations = Set.of(consultation);
    }


    public void addConsultation(Consultation consultation) {
        if (Objects.isNull(consultations)) {
            consultations = Set.of(consultation);
        }else {
            consultations.add(consultation);
        }
    }
}

package com.divjazz.recommendic.consultation.model;

import com.divjazz.recommendic.global.Auditable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.f4b6a3.ulid.UlidCreator;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "patient_diagnoses")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PatientDiagnosis extends Auditable {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name= "consultation_id")
    @JsonIgnore
    private Consultation consultation;
    private String code;
    private String codingSystem;
    private String name;
    @Enumerated(EnumType.STRING)
    private Diagnosis.DiagnosisStatus status;
    @Enumerated(EnumType.STRING)
    private Diagnosis.DiagnosisRole type;
    private String clinicalNote;
    @Builder.Default
    private final String diagnosisId = "DIAG-"+ UlidCreator.getMonotonicUlid();
}

package com.divjazz.recommendic.consultation.model;

import com.divjazz.recommendic.consultation.dto.PatientData;
import com.divjazz.recommendic.user.model.Patient;
import com.fasterxml.jackson.annotation.JsonBackReference;
import io.hypersistence.utils.hibernate.type.array.ListArrayType;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "consultation_patient_data")
public class ConsultationPatientData {
    @JoinColumn(name = "id")
    @MapsId
    @JsonBackReference
    @OneToOne
    Patient patient;
    @Id
    private long id;
    @Type(ListArrayType.class)
    @Column(name = "allergies", columnDefinition = "text[]")
    private List<String> allergies;
    @Type(ListArrayType.class)
    @Column(name = "conditions", columnDefinition = "text[]")
    private List<String> conditions;
    @Column(name = "last_visit")
    private LocalDateTime lastVisit;
    private String insurance;
    @Type(JsonBinaryType.class)
    @Column(name = "last_recorded_vitals", columnDefinition = "jsonb")
    private PatientData.RecordedVitals lastRecordedVitals;
    @Type(JsonBinaryType.class)
    @Column(name = "connected_devices", columnDefinition = "jsonb")
    private List<PatientData.ConnectedDevice> connectedDevices;
    @Type(JsonBinaryType.class)
    @Column(name = "patient_reported", columnDefinition = "jsonb")
    private PatientData.PatientReported patientReported;
}

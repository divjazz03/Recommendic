package com.divjazz.recommendic.medication.model;

import com.divjazz.recommendic.consultation.model.Consultation;
import com.divjazz.recommendic.global.Auditable;
import com.divjazz.recommendic.user.model.Patient;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

import java.util.Set;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Prescription extends Auditable {
    @Column(name = "prescriptionId", updatable = false, insertable = false)
    @Generated(event = EventType.INSERT)
    private String prescriptionId;
    @OneToMany(mappedBy = "prescription", cascade = CascadeType.PERSIST)
    @Setter
    private Set<Medication> medications;
    @JoinColumn(name = "consultation_id")
    @ManyToOne (optional = false)
    private Consultation consultation;
    @Column(name = "diagnosis")
    private String diagnosis;
    @Column(name = "self_reported")
    private boolean selfReported;
    @Column(name = "prescriber_id")
    private String prescriberId;
    @JoinColumn (name = "prescribed_to")
    @ManyToOne (optional = false)
    private Patient prescribedTo;
}

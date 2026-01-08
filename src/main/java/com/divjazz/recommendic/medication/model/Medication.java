package com.divjazz.recommendic.medication.model;

import com.divjazz.recommendic.global.Auditable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

import java.time.LocalDate;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Medication extends Auditable {
    @Column(name = "medication_id", insertable = false, updatable = false)
    @Generated(event = EventType.INSERT)
    private String medicationId;
    @Column(name = "name")
    private String name;
    @Column(name = "dosage")
    private String dosage;
    @Column(name = "frequency")
    private String  frequency;
    @Column(name = "start_date")
    private LocalDate startDate;
    @Column(name = "end_date")
    private LocalDate endDate;
    @Column(name = "instructions")
    private String instructions;
    @ManyToOne(optional = false)
    @JoinColumn(name = "prescription_id")
    private Prescription prescription;
}

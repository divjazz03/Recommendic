package com.divjazz.recommendic.consultation.model;

import com.divjazz.recommendic.Auditable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "appointment")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Appointment extends Auditable {

    @OneToOne(optional = false)
    @JoinColumn(name = "consultation_id")
    private Consultation consultation;
    @Column(name = "note")
    private String note;

    @Column(name = "status")
    private String status;
}

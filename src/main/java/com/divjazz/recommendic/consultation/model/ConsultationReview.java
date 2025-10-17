package com.divjazz.recommendic.consultation.model;

import com.divjazz.recommendic.global.Auditable;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.time.ZonedDateTime;

@Table(name = "consultation_review")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
public class ConsultationReview extends Auditable {
    @OneToOne
    @JoinColumn(name = "consultation_id", updatable = false, nullable = false)
    private Consultation consultation;
    @Max(value = 5)
    @Column(name = "rating", nullable = false, updatable = false)
    private int rating;
    @Size(max = 1000, message = "Should not be greater than 1000 characters")
    private String comment;
    @Column(name = "name", nullable = false, updatable = false)
    private String name;
    private OffsetDateTime date;
}

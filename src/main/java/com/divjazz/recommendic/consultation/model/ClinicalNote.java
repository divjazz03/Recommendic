package com.divjazz.recommendic.consultation.model;

import com.divjazz.recommendic.consultation.enums.DocumentationStatus;
import com.divjazz.recommendic.global.Auditable;
import com.divjazz.recommendic.user.model.Consultant;
import com.divjazz.recommendic.user.model.Patient;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.f4b6a3.ulid.UlidCreator;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Entity
@Table(name = "clinical_notes")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClinicalNote{
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    @Id
    private Long id;
    @Column(name = "note_id", updatable = false)
    @Builder.Default
    private final String noteId = "NT-"+ UlidCreator.getMonotonicUlid();
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "consultation_id")
    @JsonIgnore
    private Consultation consultation;
    @Column(name = "patient_id")
    private String patientId;
    @Column(name = "consultant_id")
    private String consultantId;
    private Integer version;
    private String amendmentReason;
    @Type(JsonBinaryType.class)
    @Column(name = "subjective", columnDefinition = "jsonb")
    private Subjective subjective;
    @Type(JsonBinaryType.class)
    @Column(name = "objective", columnDefinition = "jsonb")
    private Objective objective;
    @Type(JsonBinaryType.class)
    @Column(name = "assessment", columnDefinition = "jsonb")
    private Assessment assessment;
    @Column(name = "plan", columnDefinition = "jsonb")
    @Type(JsonBinaryType.class)
    private Plan plan;
    @Type(JsonBinaryType.class)
    @Column(name = "quick_note", columnDefinition = "jsonb")
    private QuickNote quickNote;
    @LastModifiedDate
    private LocalDateTime lastSavedAt;
    private String lastEditedBy;
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private DocumentationStatus status = DocumentationStatus.DRAFT;

}

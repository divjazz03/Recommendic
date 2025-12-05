package com.divjazz.recommendic.user.model.userAttributes;

import com.divjazz.recommendic.user.enums.BloodType;
import com.divjazz.recommendic.user.model.Patient;
import com.fasterxml.jackson.annotation.JsonBackReference;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@EntityListeners({AuditingEntityListener.class})
@Builder
@Getter
@Setter
@Entity
@Table(name = "patient_profiles")
public class PatientProfile{
    @Id
    private long id;

    @NotNull
    @Column(name = "created_by", updatable = false, nullable = false)
    @CreatedBy
    private String createdBy;
    @NotNull
    @Column(name = "updated_by", nullable = false)
    @LastModifiedBy
    private String updatedBy;
    @Column(name = "created_at", nullable = false, updatable = false)
    @CreatedDate
    @NotNull
    private LocalDateTime createdAt;
    @Column(name = "updated_at", nullable = false)
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Type(JsonBinaryType.class)
    @Column(name = "username", nullable = false, columnDefinition = "jsonb")
    private UserName userName;

    @Column
    private String phoneNumber;
    @Column(name = "emergency_contact_name")
    private String emergencyContactName;
    @Column(name = "emergency_contact_phone_number")
    private String emergencyContactNumber;

    @Column(name = "medical_history", columnDefinition = "jsonb")
    @Type(JsonBinaryType.class)
    private MedicalHistory medicalHistory;

    @Column(name = "lifestyle_info", columnDefinition = "jsonb")
    @Type(JsonBinaryType.class)
    private LifeStyleInfo lifeStyleInfo;

    @Type(JsonBinaryType.class)
    @Column(name = "address",  columnDefinition = "jsonb")
    private Address address;

    @Type(JsonBinaryType.class)
    @Column(name = "profile_picture",  columnDefinition = "jsonb")
    private ProfilePicture profilePicture;



    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    @JsonBackReference
    private Patient patient;
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    public String getAge() {
        if (Objects.isNull(dateOfBirth)) return "";
        return getAge(this.dateOfBirth);
    }
    public static String getAge(LocalDate dateOfBirth) {
        return String.valueOf(LocalDate.now().getYear() - dateOfBirth.getYear());
    }

}

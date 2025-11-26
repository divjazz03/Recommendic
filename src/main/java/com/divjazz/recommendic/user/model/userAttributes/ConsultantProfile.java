package com.divjazz.recommendic.user.model.userAttributes;

import com.divjazz.recommendic.global.Auditable;
import com.divjazz.recommendic.user.model.Consultant;
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

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "consultant_profiles")
@EntityListeners({AuditingEntityListener.class})
@AllArgsConstructor
@Builder(toBuilder = true)
@Getter
@Setter
@NoArgsConstructor
public class ConsultantProfile{
    @Id
    private long id;


    @Column(name = "created_by", updatable = false, nullable = false)
    @CreatedBy
    @NotNull
    private String createdBy;

    @Column(name = "updated_by", nullable = false)
    @LastModifiedBy
    @NotNull
    private String updatedBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreatedDate
    @NotNull
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    @NotNull
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Type(JsonBinaryType.class)
    @Column(name = "username", nullable = false, columnDefinition = "jsonb")
    private UserName userName;

    @Column(nullable = false)
    private String phoneNumber;

    @Type(JsonBinaryType.class)
    @Column(name = "address", nullable = false, columnDefinition = "jsonb")
    private Address address;

    @Type(JsonBinaryType.class)
    @Column(name = "profile_picture", nullable = false, columnDefinition = "jsonb")
    private ProfilePicture profilePicture;

    @Column(name = "location")
    private String locationOfInstitution;

    @Column(name = "experience")
    private int yearsOfExperience;

    @Column(name = "title")
    private String title;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "languages", columnDefinition = "text[]")
    private String[] languages;

    @Column(columnDefinition = "text")
    private String bio;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JsonBackReference
    @JoinColumn(name = "id")
    private Consultant consultant;
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;


    public String getAge() {
        if (Objects.isNull(dateOfBirth)) return "";
        return String.valueOf(LocalDate.now().getYear() - dateOfBirth.getYear());
    }

    public String[] getLanguages() {
        if (Objects.nonNull(languages)){
            return languages;
        }
        return new String[0];
    }
}

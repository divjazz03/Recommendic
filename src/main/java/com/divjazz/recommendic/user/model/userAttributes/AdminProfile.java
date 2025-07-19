package com.divjazz.recommendic.user.model.userAttributes;

import com.divjazz.recommendic.user.model.Admin;
import com.divjazz.recommendic.user.model.Patient;
import com.fasterxml.jackson.annotation.JsonBackReference;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@EntityListeners({AuditingEntityListener.class})
@Builder
@Getter
@Setter
@Entity
@Table(name = "admin_profiles")
public class AdminProfile {
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

    @Column(nullable = false)
    private String phoneNumber;

    @Type(JsonBinaryType.class)
    @Column(name = "address", nullable = false, columnDefinition = "jsonb")
    private Address address;

    @Type(JsonBinaryType.class)
    @Column(name = "profile_picture", nullable = false, columnDefinition = "jsonb")
    private ProfilePicture profilePicture;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    @JsonBackReference
    private Admin admin;
}

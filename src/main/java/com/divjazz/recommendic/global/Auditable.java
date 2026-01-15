package com.divjazz.recommendic.global;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Getter
@MappedSuperclass
@EntityListeners({AuditingEntityListener.class})
public abstract class Auditable {

    @Id
    @Column(name = "id", updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    private Instant createdAt;
    @Column(name = "updated_at", nullable = false)
    @LastModifiedDate
    private Instant updatedAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Auditable that = (Auditable) o;
        return id != 0 && id == that.id;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }

}

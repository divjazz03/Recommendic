package com.divjazz.recommendic;


import com.divjazz.recommendic.user.domain.RequestContext;
import com.divjazz.recommendic.user.exceptions.UserNotFoundException;
import jakarta.persistence.*;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class Auditable {

    @Id
    @Column(name = "id", updatable = false)
    @SequenceGenerator(name = "primary_key_seq", sequenceName = "primary_key_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "primary_key_seq")
    private long id;
    @Column(name = "reference_id")
    private String referenceId;
    @NotNull
    @Column(name = "created_by", updatable = false, nullable = false)
    private long createdBy;
    @NotNull
    @Column(name = "updated_by", nullable = false)
    private long updatedBy;
    @Column(name = "created_at",nullable = false, updatable = false)
    @CreatedDate
    @NotNull
    private LocalDateTime createdAt;
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected Auditable(){
        this.referenceId = UUID.randomUUID().toString();
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String  getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(long createdBy) {
        this.createdBy = createdBy;
    }

    public long getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(long updatedBy) {
        this.updatedBy = updatedBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    @PrePersist
    public void beforePersist(){
        long userId = RequestContext.getUserId();
        setReferenceId(UUID.randomUUID().toString());
        setCreatedAt(LocalDateTime.now());
        setCreatedBy(userId);
        setUpdatedBy(userId);
        setUpdatedAt(LocalDateTime.now());
    }

    @PreUpdate
    public void beforeUpdate(){
        long userId = RequestContext.getUserId();
        setUpdatedBy(userId);
        setUpdatedAt(LocalDateTime.now());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Auditable auditable = (Auditable) o;

        if (id != auditable.id) return false;
        if (createdBy != auditable.createdBy) return false;
        if (updatedBy != auditable.updatedBy) return false;
        if (!Objects.equals(referenceId, auditable.referenceId))
            return false;
        if (!Objects.equals(createdAt, auditable.createdAt)) return false;
        return Objects.equals(updatedAt, auditable.updatedAt);
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));

    }
}

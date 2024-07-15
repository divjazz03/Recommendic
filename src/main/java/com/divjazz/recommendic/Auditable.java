package com.divjazz.recommendic;


import com.divjazz.recommendic.user.domain.RequestContext;
import com.divjazz.recommendic.user.exceptions.UserNotFoundException;
import jakarta.persistence.*;

import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class Auditable {

    @Id
    @Column(name = "id", updatable = false)
    private UUID id;
    private UUID referenceId;
    @NotNull
    private UUID createdBy;
    @NotNull
    private UUID updatedBy;
    @Column(name = "created_at",nullable = false, updatable = false)
    @CreatedDate
    @NotNull
    private LocalDateTime createdAt;
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    private static final String  CANNOT_PERSIST_BECAUSE_NO_USER_ID_IN_CONTEXT_ERROR = "Cannot persist entity without user id in the context of this thread";
    private static final String  CANNOT_UPDATE_BECAUSE_NO_USER_ID_IN_CONTEXT_ERROR = "Cannot update entity without user id in the context of this thread";


    protected Auditable(){}

    public Auditable(UUID referenceId){
        this.referenceId = referenceId;
    }
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(UUID referenceId) {
        this.referenceId = referenceId;
    }

    public UUID getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UUID createdBy) {
        this.createdBy = createdBy;
    }

    public UUID getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(UUID updatedBy) {
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
        UUID userId = RequestContext.getUserId();
        if (Objects.nonNull(userId)){
            setCreatedAt(LocalDateTime.now());
            setCreatedBy(userId);
            setUpdatedBy(userId);
            setUpdatedAt(LocalDateTime.now());
        }
        throw new UserNotFoundException(CANNOT_PERSIST_BECAUSE_NO_USER_ID_IN_CONTEXT_ERROR);
    }

    @PreUpdate
    public void beforeUpdate(){
        UUID userId = RequestContext.getUserId();
        if (Objects.nonNull(userId)){
            setCreatedAt(LocalDateTime.now());
            setCreatedBy(userId);
            setUpdatedBy(userId);
            setUpdatedAt(LocalDateTime.now());
        }
        throw new UserNotFoundException(CANNOT_UPDATE_BECAUSE_NO_USER_ID_IN_CONTEXT_ERROR);
    }
}

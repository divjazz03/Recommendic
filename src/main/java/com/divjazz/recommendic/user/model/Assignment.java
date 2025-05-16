package com.divjazz.recommendic.user.model;

import com.divjazz.recommendic.Auditable;
import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "assignment")
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class Assignment extends Auditable implements Serializable {


    @ManyToOne(optional = false)
    @JoinColumn(name = "admin_id")
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    @JsonProperty("admin_id")
    private Admin adminAssigned;

    protected Assignment() {
    }

    public Assignment(UUID id, UUID referenceId, Admin adminAssigned) {
        this.adminAssigned = adminAssigned;
    }


    public Admin getAdminAssigned() {
        return adminAssigned;
    }

    public void setAdminAssigned(Admin adminAssigned) {
        this.adminAssigned = adminAssigned;
    }


}

package com.divjazz.recommendic.user.model;

import com.divjazz.recommendic.Auditable;
import com.divjazz.recommendic.user.model.Admin;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.io.Serializable;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "assignment")
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class Assignment extends Auditable implements Serializable {


    @ManyToOne(optional = false)
    @JoinColumn(name = "admin_id")
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    @JsonProperty("user_id")
    private Admin adminAssigned ;

    protected Assignment(){}

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

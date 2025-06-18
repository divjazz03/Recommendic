package com.divjazz.recommendic.user.model;

import com.divjazz.recommendic.Auditable;
import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "assignment")
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class Assignment extends Auditable implements Serializable {


    @ManyToOne(optional = false)
    @JoinColumn(name = "admin_id")
    private Admin adminAssigned;

    public void setAdminAssigned(Admin adminAssigned) {
        this.adminAssigned = adminAssigned;
    }


}

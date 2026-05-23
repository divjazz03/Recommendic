package com.divjazz.recommendic.user.model;

import com.divjazz.recommendic.global.Auditable;
import com.fasterxml.jackson.annotation.*;
import com.github.f4b6a3.ulid.UlidCreator;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "assignments")
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class Assignment extends Auditable implements Serializable {
    @Column(name = "assignment_id")
    private final String assignmentId = "ASS-" + UlidCreator.getMonotonicUlid();
    @ManyToOne(optional = false)
    @JoinColumn(name = "admin_id")
    private Admin adminAssigned;


}

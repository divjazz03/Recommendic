package com.divjazz.recommendic.user.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "medical_category")
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class MedicalCategoryEntity {
    @Id
    private long id;
    @Column(name = "name")
    private String name;
    @Column(name = "description")
    private String description;
}

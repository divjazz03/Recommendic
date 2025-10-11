package com.divjazz.recommendic.user.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

import java.util.Objects;

@Entity
@Table(name = "medical_category")
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class MedicalCategoryEntity {
    @Id
    @Generated(event = EventType.INSERT)
    private long id;
    @Column(name = "name")
    private String name;
    @Column(name = "description")
    private String description;


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (this == o) return true;

        MedicalCategoryEntity that = (MedicalCategoryEntity) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}

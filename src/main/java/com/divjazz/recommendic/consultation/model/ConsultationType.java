package com.divjazz.recommendic.consultation.model;

import com.divjazz.recommendic.Auditable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "consultation_type")
@Getter
public class ConsultationType extends Auditable {
    @Column(name = "name", nullable = false, updatable = false)
    private String name;
    @Setter
    @Column(name = "price", nullable = false)
    private BigDecimal price;
    private String description;
}

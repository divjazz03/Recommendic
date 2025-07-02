package com.divjazz.recommendic.user.model.certification;

import com.divjazz.recommendic.global.Auditable;
import com.divjazz.recommendic.user.model.Consultant;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "consultant_education")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ConsultantEducation extends Auditable {
    @ManyToOne(cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "consultant_id", updatable = false, nullable = false)
    private Consultant consultant;
    @Column(name = "degree", nullable = false)
    private String degree;
    @Column(name = "institution", nullable = false)
    private String institution;
    @Column(name = "year", nullable = false)
    private int year;
}

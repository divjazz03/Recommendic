package com.divjazz.recommendic.user.model.certification;

import com.divjazz.recommendic.global.Auditable;
import com.divjazz.recommendic.user.model.Consultant;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "consultant_education")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ConsultantEducation extends Auditable {
    @ManyToOne(optional = false)
    @JoinColumn(name = "consultant_id",nullable = false)
    private Consultant consultant;
    @Column(name = "degree", nullable = false)
    private String degree;
    @Column(name = "institution", nullable = false)
    private String institution;
    @Column(name = "year", nullable = false)
    private int year;

    public static ConsultantEducation ofEmpty() {
        return new ConsultantEducation(null, null,null,0);
    }
}

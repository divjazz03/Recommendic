package com.divjazz.recommendic.user.model.userAttributes;

import com.divjazz.recommendic.Auditable;
import com.divjazz.recommendic.user.model.Consultant;
import io.hypersistence.utils.hibernate.type.array.IntArrayType;
import io.hypersistence.utils.hibernate.type.array.StringArrayType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.ArrayUtils;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "consultant_stat")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ConsultantStat extends Auditable {
    @OneToOne(optional = false)
    @JoinColumn(name = "consultant_id")
    private Consultant consultant;

    @Type(StringArrayType.class)
    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "patients_helped")
    private String[] patientsHelped;
    @Type(StringArrayType.class)
    @JdbcTypeCode(SqlTypes.ARRAY)
    private String[] successes;

    @Column(name = "success_rate")
    private int successRate;

    @Column(name = "response_times")
    @JdbcTypeCode(SqlTypes.ARRAY)
    @Type(IntArrayType.class)
    private int[] responseTimes;

    @Column(name = "average_response")
    private Integer averageResponseTime;

    @Column(name = "follow_ups")
    @JdbcTypeCode(SqlTypes.ARRAY)
    @Type(StringArrayType.class)
    private String[] followUps ;


    public void addPatientsHelpedIds (String patientsHelpedId) {
        patientsHelped = ArrayUtils.add(patientsHelped, patientsHelpedId);
    }
    public void addSuccessFulConsultation (String successConsultationId ) {
        successes = ArrayUtils.add(successes, successConsultationId);
    }
    public void addResponseTime (int responseTime) {
        this.responseTimes = ArrayUtils.add(responseTimes, responseTime);
    }
    public void addFollowUpId (String followUpId) {
        followUps = ArrayUtils.add(followUps, followUpId);
    }
}

package com.divjazz.recommendic.user.model.userAttributes;

import com.divjazz.recommendic.global.Auditable;
import com.divjazz.recommendic.user.model.Consultant;
import io.hypersistence.utils.hibernate.type.array.IntArrayType;
import io.hypersistence.utils.hibernate.type.array.StringArrayType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
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

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "patients_helped", columnDefinition = "text[]")
    private String[] patientsHelped;
    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "successes", columnDefinition = "text[]")
    private String[] successes;

    @Column(name = "success_rate")
    private int successRate;

    @Column(name = "response_times", columnDefinition = "integer[]")
    @JdbcTypeCode(SqlTypes.ARRAY)
    private int[] responseTimes;

    @Column(name = "average_response")
    private Integer averageResponseTime;

    @Column(name = "follow_ups", columnDefinition = "text[]")
    @JdbcTypeCode(SqlTypes.ARRAY)
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

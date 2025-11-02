package com.divjazz.recommendic.security.model;

import com.divjazz.recommendic.global.Auditable;
import com.divjazz.recommendic.user.model.Consultant;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "consultant_security_setting")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ConsultantUserSecuritySetting extends Auditable {
    @Column(name = "multi_factor_auth_enabled")
    private boolean multiFactorAuthEnabled;
    @Column(name = "session_timeout_min")
    private int sessionTimeoutMin;
    @Column(name = "login_alerts_enabled")
    private boolean loginAlertsEnabled;
    @OneToOne
    @JoinColumn(name = "consultant_id")
    private Consultant consultant;
}

package com.divjazz.recommendic.user.enums;

import static com.divjazz.recommendic.security.constant.Constants.*;

public enum Authority {
    PATIENT(PATIENT_AUTHORITIES),
    ADMIN(ADMIN_AUTHORITIES),
    SUPER_ADMIN(SUPER_ADMIN_AUTHORITIES),
    CONSULTANT(CONSULTANT_AUTHORITIES);

    private final String value;

    Authority(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

package com.divjazz.recommendic.user.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.Objects;

@Getter
public enum CertificateType {
    RESUME("resume"), CERTIFICATE("certificate") ;
    @JsonValue
    private final String value;
    CertificateType(String value) {
        this.value = value;
    }

    @JsonCreator
    public static CertificateType fromValue(String value) {
        if (Objects.isNull(value)) {
            throw new IllegalArgumentException("Value of Certificate type should not be null");
        }

        for (CertificateType certificateType : CertificateType.values()) {
            if (certificateType.value.equalsIgnoreCase(value)) {
                return certificateType;
            }
        }
        throw new IllegalArgumentException("Certificate type %s is invalid".formatted(value));
    }


}

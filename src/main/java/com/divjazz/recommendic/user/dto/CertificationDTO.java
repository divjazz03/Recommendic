package com.divjazz.recommendic.user.dto;

import com.divjazz.recommendic.user.enums.CertificateType;

public record CertificationDTO(
        String name,
        String fileUrl,
        CertificateType type
) {
}

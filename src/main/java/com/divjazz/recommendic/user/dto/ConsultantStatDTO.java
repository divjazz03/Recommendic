package com.divjazz.recommendic.user.dto;

public record ConsultantStatDTO(
        int patientsHelped,
        int successRate,
        int responseTimeInMinutes,
        int followUpRate
) {
}

package com.divjazz.recommendic.user.dto;

public sealed interface UserResponse permits ConsultantInfoResponse, AdminResponse, PatientInfoResponse {
}

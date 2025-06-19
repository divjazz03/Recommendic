package com.divjazz.recommendic.user.dto;

public sealed interface UserInfoResponse permits ConsultantInfoResponse, AdminInfoResponse, PatientInfoResponse {
}

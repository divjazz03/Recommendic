package com.divjazz.recommendic.user.dto;

public sealed interface UserResponse permits ConsultantResponse, AdminResponse, PatientResponse {
}

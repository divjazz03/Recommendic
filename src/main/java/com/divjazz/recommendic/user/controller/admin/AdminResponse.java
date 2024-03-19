package com.divjazz.recommendic.user.controller.admin;

import java.time.LocalDate;

public record AdminResponse(String email, String password, LocalDate dateOfExpiry) {
}

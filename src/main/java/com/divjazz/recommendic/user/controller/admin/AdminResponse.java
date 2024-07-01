package com.divjazz.recommendic.user.controller.admin;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record AdminResponse(String email, String password, LocalDateTime dateOfExpiry) {
}

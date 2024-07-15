package com.divjazz.recommendic.user.controller.admin;

public record GenerateAdminPasswordResponse(String encryptedPassword, String normalPassword) {
}

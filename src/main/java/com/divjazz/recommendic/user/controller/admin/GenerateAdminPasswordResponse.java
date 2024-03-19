package com.divjazz.recommendic.user.controller.admin;

import com.divjazz.recommendic.user.model.userAttributes.AdminPassword;

public record GenerateAdminPasswordResponse(AdminPassword encryptedPassword, String normalPassword) {
}

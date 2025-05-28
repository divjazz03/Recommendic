package com.divjazz.recommendic.user.repository.projection;

import com.divjazz.recommendic.user.model.userAttributes.credential.UserCredential;

public record UserSecurityProjection(Long id, String email, String userId, UserCredential userCredential) {
}

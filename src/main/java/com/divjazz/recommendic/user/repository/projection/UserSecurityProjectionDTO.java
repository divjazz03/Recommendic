package com.divjazz.recommendic.user.repository.projection;

import com.divjazz.recommendic.user.model.userAttributes.credential.UserCredential;

public record UserSecurityProjectionDTO(Long id, String email, String userId, UserCredential userCredential) {

}

package com.divjazz.recommendic.user.repository.projection;

import com.divjazz.recommendic.user.model.userAttributes.credential.UserCredential;

public interface UserSecurityProjection {
    Long getId();
    String getEmail();
    String getUserId();
    UserCredential getUserCredential();
}


package com.divjazz.recommendic.user.repository.projection;

import com.divjazz.recommendic.user.model.userAttributes.Role;
import com.divjazz.recommendic.user.model.userAttributes.credential.UserCredential;

public interface UserSecurityProjection {
    Long getId();
    String getEmail();
    Role getRole();
    String getUserId();
    UserCredential getUserCredential();
}

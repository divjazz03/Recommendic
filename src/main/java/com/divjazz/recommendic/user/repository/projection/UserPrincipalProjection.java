package com.divjazz.recommendic.user.repository.projection;

import com.divjazz.recommendic.security.UserPrincipal;
import com.divjazz.recommendic.user.model.userAttributes.credential.UserCredential;

public interface UserPrincipalProjection {
    boolean isEnabled();
    String getUsername();
    String getEmail();
    RoleProjection getRole();
    UserCredential getUserCredential();

    default UserPrincipal toUserPrincipal() {
        var userPrincipal = new UserPrincipal(
                getEmail(),
                getUserCredential(),
                getRole().toRole()
        );
        userPrincipal.setEnabled(isEnabled());
        return userPrincipal;
    }
}

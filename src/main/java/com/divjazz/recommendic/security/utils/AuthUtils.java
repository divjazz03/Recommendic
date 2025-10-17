package com.divjazz.recommendic.security.utils;

import com.divjazz.recommendic.security.SessionUser;
import com.divjazz.recommendic.security.UserPrincipal;
import com.divjazz.recommendic.security.domain.CurrentUserHolder;
import com.divjazz.recommendic.user.dto.UserDTO;
import com.divjazz.recommendic.user.model.User;
import com.divjazz.recommendic.user.repository.projection.UserProjection;
import com.divjazz.recommendic.user.service.GeneralUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class AuthUtils {

    private final CurrentUserHolder userHolder;
    private final GeneralUserService generalUserService;

    public UserDTO getCurrentUser() {
        if (Objects.isNull(userHolder.getUser())) {
            var authenticationToken = SecurityContextHolder.getContext().getAuthentication();
            if (authenticationToken.getPrincipal() instanceof UserPrincipal userPrincipal) {
                userHolder.setUser(generalUserService.retrieveUserByEmail(userPrincipal.getUsername()).toUserDTO());
            } else if (authenticationToken.getPrincipal() instanceof SessionUser sessionUser) {
                var user = generalUserService.retrieveUserByEmail(sessionUser.getEmail()).toUserDTO();
                userHolder.setUser(user);
            }
        }
        return userHolder.getUser();
    }
}

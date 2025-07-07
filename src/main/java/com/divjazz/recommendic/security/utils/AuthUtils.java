package com.divjazz.recommendic.security.utils;

import com.divjazz.recommendic.security.domain.CurrentUserHolder;
import com.divjazz.recommendic.user.model.User;
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

    public User getCurrentUser() {
        if (Objects.isNull(userHolder.getUser())) {
            var authenticationToken = SecurityContextHolder.getContext().getAuthentication();
            if (authenticationToken.getPrincipal() instanceof String principal) {
                userHolder.setUser(generalUserService.retrieveUserByEmail(principal));
            } else if (authenticationToken.getPrincipal() instanceof User user){
                userHolder.setUser(user);
            }
        }
        return userHolder.getUser();
    }
}

package com.divjazz.recommendic.global.security.utils;

import com.divjazz.recommendic.global.security.domain.CurrentUserHolder;
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
            var email =  (String) authenticationToken.getPrincipal();
            userHolder.setUser(generalUserService.retrieveUserByEmail(email));
        }
        return userHolder.getUser();
    }
}

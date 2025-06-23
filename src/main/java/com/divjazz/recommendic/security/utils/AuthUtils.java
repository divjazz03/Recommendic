package com.divjazz.recommendic.security.utils;

import com.divjazz.recommendic.security.ApiAuthentication;
import com.divjazz.recommendic.security.domain.CurrentUserHolder;
import com.divjazz.recommendic.user.controller.UserController;
import com.divjazz.recommendic.user.model.User;
import com.divjazz.recommendic.user.service.GeneralUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

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

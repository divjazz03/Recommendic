package com.divjazz.recommendic.security.utils;

import com.divjazz.recommendic.security.ApiAuthentication;
import com.divjazz.recommendic.user.model.User;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthUtils {

    public User getCurrentUser() {
        var authentication = (ApiAuthentication) SecurityContextHolder.getContext().getAuthentication();
        return (User) authentication.getPrincipal();
    }
}

package com.divjazz.recommendic.security;

import com.divjazz.recommendic.user.service.GeneralUserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class CustomUserDetailsService implements UserDetailsService {

    private final GeneralUserService userService;

    public CustomUserDetailsService(GeneralUserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userService.retrieveUserByUsername(username);

    }
}

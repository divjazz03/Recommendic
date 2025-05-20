package com.divjazz.recommendic.security;

import com.divjazz.recommendic.user.exception.UserNotFoundException;
import com.divjazz.recommendic.user.service.GeneralUserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final GeneralUserService userService;



    public CustomUserDetailsService(GeneralUserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userService.retrieveUserByEmail(username);

    }

    public UserDetails loadUserByUserId(String userId) throws UserNotFoundException {
        return userService.retrieveUserByUserId(userId);
    }
}

package com.divjazz.recommendic.security;

import com.divjazz.recommendic.user.model.User;
import org.springframework.security.authentication.*;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.function.Consumer;


public class CustomAuthenticationProvider extends DaoAuthenticationProvider {

    public CustomAuthenticationProvider( PasswordEncoder passwordEncoder,UserDetailsService userDetailsService) {
        super(passwordEncoder);
        super.setUserDetailsService(userDetailsService);
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        var user =(User) getUserDetailsService().loadUserByUsername(((ApiAuthentication) authentication).getEmail());
        var credential = user.getUserCredential();
        if (credential.isExpired()) {
            throw new CredentialsExpiredException("Credentials are expired, please reset your password");
        }
        return super.authenticate(authentication);

    }

    @Override
    public boolean supports(Class<?> authentication) {
        return ApiAuthentication.class.isAssignableFrom(authentication);
    }

}

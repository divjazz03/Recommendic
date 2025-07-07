package com.divjazz.recommendic.security;

import com.divjazz.recommendic.user.service.GeneralUserService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;


public class CustomAuthenticationProvider extends DaoAuthenticationProvider {

    private final GeneralUserService generalUserService;

    public CustomAuthenticationProvider(PasswordEncoder passwordEncoder, UserDetailsService userDetailsService, GeneralUserService userService) {
        super(passwordEncoder);
        super.setUserDetailsService(userDetailsService);
        this.generalUserService = userService;
    }

    @Override
    public Authentication authenticate(Authentication authentication){
        var userCredentials = generalUserService.retrieveUserCredentials((String) authentication.getPrincipal());
        if (userCredentials.isExpired()) {
            throw new CredentialsExpiredException("Credentials are expired, please reset your password");
        }
        try {
            return super.authenticate(authentication);
        } catch (AuthenticationException authenticationException) {
            if (authenticationException instanceof BadCredentialsException ex) {
                logger.error(ex.getMessage());
                throw new com.divjazz.recommendic.security.exception.AuthenticationException("Login Failed: %s".formatted(ex.getMessage()));
            }
            if (authenticationException instanceof DisabledException ex) {
                logger.error(ex.getMessage());
                throw ex;
            }
            throw authenticationException;
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return ApiAuthentication.class.isAssignableFrom(authentication);
    }

}

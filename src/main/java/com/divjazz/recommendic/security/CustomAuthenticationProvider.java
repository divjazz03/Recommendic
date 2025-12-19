package com.divjazz.recommendic.security;

import com.divjazz.recommendic.user.service.GeneralUserService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.*;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Primary
@Profile("prod")
public class CustomAuthenticationProvider implements AuthenticationProvider {


    private final GeneralUserService generalUserService;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;

    public CustomAuthenticationProvider(GeneralUserService generalUserService, PasswordEncoder passwordEncoder, UserDetailsService userDetailsService) {
        this.generalUserService = generalUserService;
        this.passwordEncoder = passwordEncoder;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Authentication authenticate(Authentication authentication){
        var userCredentials = generalUserService.retrieveUserCredentials((String) authentication.getPrincipal());
        var userDetails = userDetailsService.loadUserByUsername(authentication.getName());
            if (!passwordEncoder.matches((String)authentication.getCredentials(), userCredentials.getPassword() )) {
                throw new BadCredentialsException("Invalid Credentials");
            }
            if (userCredentials.isExpired()) {
                throw new CredentialsExpiredException("Credentials are expired, please reset your password");
            }
            if (!userDetails.isAccountNonExpired()) {
                throw new AccountExpiredException("Your account has expired");
            }
            if (!userDetails.isAccountNonLocked()) {
                throw new LockedException("Your account is currently locked");
            }

            return UsernamePasswordAuthenticationToken.authenticated(userDetails, "",userDetails.getAuthorities() );
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

}

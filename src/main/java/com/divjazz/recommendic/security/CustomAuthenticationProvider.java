package com.divjazz.recommendic.security;

import com.divjazz.recommendic.user.model.User;
import com.divjazz.recommendic.user.repository.credential.UserCredentialRepository;
import com.divjazz.recommendic.user.service.GeneralUserService;
import org.springframework.security.authentication.*;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.function.Consumer;


public class CustomAuthenticationProvider extends DaoAuthenticationProvider {

    private final PasswordEncoder passwordEncoder;
    private final Consumer<User> validAccount = user -> {
        if (!user.isAccountNonLocked()) {
            throw new LockedException("Your account is currently locked");
        }
        if (!user.isEnabled()) {
            throw new DisabledException("Your account is currently disabled");
        }
        if (!user.isCredentialsNonExpired()) {
            throw new CredentialsExpiredException("Your password has expired. Please update your password");
        }
        if (!user.isAccountNonExpired()) {
            throw new AccountExpiredException("Your account has expired. Please contact administrator");
        }

    };

    public CustomAuthenticationProvider( PasswordEncoder passwordEncoder,UserDetailsService userDetailsService) {
        super(passwordEncoder);
        super.setUserDetailsService(userDetailsService);
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        var user =(User) getUserDetailsService().loadUserByUsername(((ApiAuthentication) authentication).getEmail());
        var credential = user.getUserCredential();
        if (credential.getUpdatedAt().plusDays(60).isBefore(LocalDateTime.now())) {
            throw new CredentialsExpiredException("Credentials are expired, please reset your password");
        }
        validAccount.accept(user);
        if (passwordEncoder.matches(((ApiAuthentication) authentication).getPassword(), credential.getPassword())) {
            user.setLastLogin(LocalDateTime.now());
            return ApiAuthentication.authenticated(user, user.getAuthorities());
        } else
            throw new BadCredentialsException("Unable to login due to invalid credentials. Please try again");

    }

    @Override
    public boolean supports(Class<?> authentication) {
        return ApiAuthentication.class.isAssignableFrom(authentication);
    }

}

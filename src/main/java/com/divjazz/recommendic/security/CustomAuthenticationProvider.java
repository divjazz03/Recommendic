package com.divjazz.recommendic.security;

import com.divjazz.recommendic.user.model.User;
import com.divjazz.recommendic.user.repository.credential.UserCredentialRepository;
import com.divjazz.recommendic.user.service.GeneralUserService;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.function.Consumer;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final GeneralUserService userService;
    private final PasswordEncoder passwordEncoder;
    private final UserCredentialRepository userCredentialRepository;

    public CustomAuthenticationProvider(GeneralUserService userService, PasswordEncoder passwordEncoder, UserCredentialRepository userCredentialRepository) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.userCredentialRepository = userCredentialRepository;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        var userAuth = (ApiAuthentication) authentication;
        var user = userService.retrieveUserByUsername(userAuth.getEmail());
        if (Objects.nonNull(user)) {
            var credential = userCredentialRepository.getUserCredentialByUser_UserId(user.getUserId())
                    .orElseThrow(() -> new RuntimeException("Credentials not found"));
            if (credential.getUpdatedAt().minusDays(90).isAfter(LocalDateTime.now())) {
                throw new LockedException("Credentials are expired, please reset your password");
            }
            validAccount.accept(user);
            if (passwordEncoder.matches(userAuth.getPassword(), credential.getPassword())) {
                user.setLastLogin(LocalDateTime.now());
                return ApiAuthentication.authenticated(user, user.getAuthorities());
            } else
                throw new BadCredentialsException("Unable to login due to invalid credentials. Please try again");
        }
        throw new IllegalStateException("Unable to authenticate");
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return ApiAuthentication.class.isAssignableFrom(authentication);
    }

    private final Consumer<User> validAccount = user -> {
        if (!user.isAccountNonLocked()) {
            throw new LockedException("Your account is currently locked");
        }if (!user.isEnabled()) {
            throw new LockedException("Your account is currently disabled");
        }if (!user.isCredentialsNonExpired()) {
            throw new LockedException("Your password has expired. Please update your password");
        }if (!user.isAccountNonExpired()) {
            throw new LockedException("Your account has expired. Please contact administrator");
        }

    };

}

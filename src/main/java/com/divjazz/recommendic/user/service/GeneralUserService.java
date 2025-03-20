package com.divjazz.recommendic.user.service;

import com.divjazz.recommendic.cache.CacheStore;
import com.divjazz.recommendic.security.ApiAuthentication;
import com.divjazz.recommendic.user.domain.RequestContext;
import com.divjazz.recommendic.user.enums.LoginType;
import com.divjazz.recommendic.user.exception.UserNotFoundException;
import com.divjazz.recommendic.user.model.User;
import com.divjazz.recommendic.user.model.userAttributes.credential.UserCredential;
import com.divjazz.recommendic.user.repository.UserRepository;
import com.divjazz.recommendic.user.repository.credential.UserCredentialRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class GeneralUserService {

    private final UserRepository userRepository;

    private final UserCredentialRepository userCredentialRepository;

    private final CacheStore<String, Integer> loginCache;

    public GeneralUserService(
            UserRepository userRepository, UserCredentialRepository userCredentialRepository, CacheStore<String, Integer> loginCache) {

        this.userRepository = userRepository;
        this.userCredentialRepository = userCredentialRepository;
        this.loginCache = loginCache;
    }


    public User retrieveUserByEmail(String email) {
        return userRepository
                .findByEmail(email)
                .orElseThrow(UserNotFoundException::new);
    }
    public User retrieveCurrentUser() {
        ApiAuthentication authentication = (ApiAuthentication) SecurityContextHolder.getContext().getAuthentication();
        return retrieveUserByEmail(authentication.getEmail());

    }

    public User retrieveUserByUserId(String id) {
        return userRepository.findByUserId(id).orElseThrow(UserNotFoundException::new);
    }

    public User retrieveUserById(Long id) {
        return userRepository.findById(id).orElseThrow(UserNotFoundException::new);
    }

    public UserCredential retrieveCredentialById(Long id) {
        return userCredentialRepository
                .findById(id)
                .orElseThrow(() -> new RuntimeException("Credentials not Found"));
    }

    public void updateLoginAttempt(String email, LoginType loginType) {
        var user = retrieveUserByEmail(email);
        RequestContext.setUserId(user.getId());
        switch (loginType) {
            case LOGIN_ATTEMPT -> {
                if (loginCache.get(user.getEmail()) == null) {
                    user.setLoginAttempts(0);
                }
                user.setLoginAttempts(user.getLoginAttempts() + 1);
                loginCache.put(user.getEmail(), user.getLoginAttempts());
                if (loginCache.get(user.getEmail()) > 5) {
                    user.setAccountNonLocked(false);
                }
            }
            case LOGIN_SUCCESS -> {
                user.setLastLogin(LocalDateTime.now());
                loginCache.evict(user.getEmail());
            }
        }
        userRepository.save(user);
    }

    public boolean isUserNotExists(String email) {
        return !userRepository.existsByEmail(email);
    }
}






















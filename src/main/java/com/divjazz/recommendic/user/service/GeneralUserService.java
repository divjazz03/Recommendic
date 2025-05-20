package com.divjazz.recommendic.user.service;

import com.divjazz.recommendic.security.ApiAuthentication;
import com.divjazz.recommendic.user.domain.RequestContext;
import com.divjazz.recommendic.user.enums.LoginType;
import com.divjazz.recommendic.user.exception.UserNotFoundException;
import com.divjazz.recommendic.user.model.User;
import com.divjazz.recommendic.user.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class GeneralUserService {

    private final UserRepository userRepository;


    private final UserLoginRetryHandler userLoginRetryHandler;

    public GeneralUserService(
            UserRepository userRepository,
            UserLoginRetryHandler userLoginRetryHandler) {

        this.userRepository = userRepository;
        this.userLoginRetryHandler = userLoginRetryHandler;
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

    @Transactional
    public void updateLoginAttempt(User user, LoginType loginType) throws UserNotFoundException{
        RequestContext.setUserId(user.getId());
        switch (loginType) {
            case LOGIN_FAILED -> {
                userLoginRetryHandler.handleFailedAttempts(user.getEmail());
            }
            case LOGIN_SUCCESS -> {
                userLoginRetryHandler.handleSuccessFulAttempt(user.getEmail());
                user.setLastLogin(LocalDateTime.now());
                userRepository.save(user);
            }
        }
    }

    public boolean isUserNotExists(String email) {
        return !userRepository.existsByEmail(email);
    }

}






















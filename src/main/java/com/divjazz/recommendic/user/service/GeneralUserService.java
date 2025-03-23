package com.divjazz.recommendic.user.service;

import com.divjazz.recommendic.security.ApiAuthentication;
import com.divjazz.recommendic.user.domain.RequestContext;
import com.divjazz.recommendic.user.enums.LoginType;
import com.divjazz.recommendic.user.exception.UserNotFoundException;
import com.divjazz.recommendic.user.model.User;
import com.divjazz.recommendic.user.model.userAttributes.credential.UserCredential;
import com.divjazz.recommendic.user.repository.UserRepository;
import com.divjazz.recommendic.user.repository.credential.UserCredentialRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GeneralUserService {

    private final UserRepository userRepository;

    private final UserCredentialRepository userCredentialRepository;

    private final UserLoginRetryHandler userLoginRetryHandler;

    public GeneralUserService(
            UserRepository userRepository, UserCredentialRepository userCredentialRepository,
            UserLoginRetryHandler userLoginRetryHandler) {

        this.userRepository = userRepository;
        this.userCredentialRepository = userCredentialRepository;
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

    public UserCredential retrieveCredentialById(Long id) {
        return userCredentialRepository
                .findById(id)
                .orElseThrow(() -> new RuntimeException("Credentials not Found"));
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
                userRepository.save(user);
            }
        }
    }

    public boolean isUserNotExists(String email) {
        return !userRepository.existsByEmail(email);
    }

}






















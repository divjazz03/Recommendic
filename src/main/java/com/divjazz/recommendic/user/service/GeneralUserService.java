package com.divjazz.recommendic.user.service;

import com.divjazz.recommendic.security.ApiAuthentication;
import com.divjazz.recommendic.user.domain.RequestContext;
import com.divjazz.recommendic.user.enums.LoginType;
import com.divjazz.recommendic.user.exception.UserNotFoundException;
import com.divjazz.recommendic.user.model.User;
import com.divjazz.recommendic.user.model.userAttributes.credential.UserCredential;
import com.divjazz.recommendic.user.repository.UserRepository;
import com.divjazz.recommendic.user.repository.projection.UserSecurityProjection;
import com.divjazz.recommendic.user.repository.projection.UserSecurityProjectionDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class GeneralUserService {

    private final UserRepository userRepository;
    private final UserLoginRetryHandler userLoginRetryHandler;

    private final ObjectMapper objectMapper;

    public GeneralUserService(
            UserRepository userRepository,
            UserLoginRetryHandler userLoginRetryHandler,
            ObjectMapper objectMapper) {

        this.userRepository = userRepository;
        this.userLoginRetryHandler = userLoginRetryHandler;
        this.objectMapper = objectMapper;
    }


    public User retrieveUserByEmail(String email) {
        return userRepository
                .findByEmail(email)
                .orElseThrow(UserNotFoundException::new);
    }
    public UserSecurityProjection retrieveUserDetailInfoByEmail(String email){
        var result = userRepository.findByEmail_Security_Projection(email)
                .orElseThrow(UserNotFoundException::new);
        var credential = objectMapper.convertValue(result.userCredential(), UserCredential.class);

        return new UserSecurityProjection(result.id(),result.email(),result.userId(), credential);

    }
    public UserCredential retrieveUserCredentials(String email){
        return objectMapper
                .convertValue(userRepository
                        .findByEmail_ReturningCredentialsJsonB(email)
                        .orElseThrow(UserNotFoundException::new),
                        UserCredential.class);
    }


    public User retrieveCurrentUser() {
        ApiAuthentication authentication = (ApiAuthentication) SecurityContextHolder.getContext().getAuthentication();
        return (User) authentication.getPrincipal();

    }

    public User retrieveUserByUserId(String id) {
        return userRepository.findByUserId(id).orElseThrow(UserNotFoundException::new);
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






















package com.divjazz.recommendic.user.service;

import com.divjazz.recommendic.global.exception.EntityNotFoundException;
import com.divjazz.recommendic.security.SessionUser;
import com.divjazz.recommendic.security.exception.AuthenticationException;
import com.divjazz.recommendic.user.controller.UserController;
import com.divjazz.recommendic.user.controller.UserResponse;
import com.divjazz.recommendic.user.controller.consultant.payload.ConsultantProfileResponse;
import com.divjazz.recommendic.user.controller.patient.payload.PatientProfileResponse;
import com.divjazz.recommendic.user.dto.UserDTO;
import com.divjazz.recommendic.user.enums.LoginType;
import com.divjazz.recommendic.user.mapper.UserMapper;
import com.divjazz.recommendic.user.model.Admin;
import com.divjazz.recommendic.user.model.Consultant;
import com.divjazz.recommendic.user.model.Patient;
import com.divjazz.recommendic.user.model.User;
import com.divjazz.recommendic.user.model.userAttributes.credential.UserCredential;
import com.divjazz.recommendic.user.repository.*;
import com.divjazz.recommendic.user.repository.projection.UserProjection;
import com.divjazz.recommendic.user.repository.projection.UserSecurityProjectionDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class GeneralUserService {

    private final UserLoginRetryHandler userLoginRetryHandler;

    private final ObjectMapper objectMapper;
    private final PatientRepository patientRepository;
    private final PatientCustomRepository patientCustomRepository;
    private final ConsultantRepository consultantRepository;
    private final ConsultantCustomRepository consultantCustomRepository;
    private final AdminRepository adminRepository;
    private final UserRepository userRepository;
    private final UserMapper userMapper;


    public User retrieveUserByEmail(String email) {
        return findUserByEmail(email);
    }

    public UserSecurityProjectionDTO retrieveUserDetailInfoByEmail(String email) {
        var result = userRepository.findByEmail_Security_Projection(email)
                .orElseThrow(() -> new EntityNotFoundException("User with email: %s not found".formatted(email)));
        var credential = objectMapper.convertValue(result.userCredential(), UserCredential.class);

        return new UserSecurityProjectionDTO(result.id(), result.email(), result.userId(), credential);

    }

    public UserCredential retrieveUserCredentials(String email) {
        try {
            var credentialString = userRepository
                    .findByEmail_ReturningCredentialsJsonB(email)
                    .orElseThrow(() -> new EntityNotFoundException("User with email: %s not found".formatted(email)));
            return objectMapper
                    .readValue(credentialString,
                            UserCredential.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


    public UserResponse retrieveCurrentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() == null || authentication.getPrincipal().equals("anonymousUser")) {
            throw new AuthenticationException("No authentication found in context please login");
        }
        Jwt jwt = (Jwt) authentication.getPrincipal();
        var user = retrieveUserByEmail(jwt.getSubject());
        return userMapper.toUserResponse(user);
    }

    public User retrieveUserByUserId(String id) {
        return findUserByUserId(id);
    }

    @Transactional
    public void enableUser(String userId) {
        User user = findUserByUserId(userId);
        user.getUserPrincipal().setEnabled(true);
        switch (user.getUserType()) {
            case CONSULTANT -> consultantRepository.save((Consultant) user);
            case PATIENT -> patientRepository.save((Patient) user);
            case ADMIN -> adminRepository.save((Admin) user);
        }
    }

    private User findUserByEmail(String email) {
        Optional<Patient> patient = patientRepository.findByUserPrincipal_Email(email);
        if (patient.isPresent()){
            return patient.get();
        }
        Optional<Consultant> consultant = consultantRepository.findByUserPrincipal_Email(email);
        if (consultant.isPresent()) {
            return consultant.get();
        }

        throw new EntityNotFoundException("User with email: %s not found".formatted(email));
    }

    private User findUserByUserId(String userId) {
        Optional<Patient> patient = patientRepository.findByUserId(userId);
        if (patient.isPresent()){
            return patient.get();
        }
        Optional<Consultant> consultant = consultantRepository.findByUserId(userId);
        if (consultant.isPresent()) {
            return consultant.get();
        }
        Optional<Admin> admin = adminRepository.findByUserId(userId);
        if (admin.isPresent()) {
            return admin.get();
        }

        throw new EntityNotFoundException("User with id: %s not found".formatted(userId));
    }

    @Transactional
    public void updateLoginAttempt(User user, LoginType loginType) throws EntityNotFoundException {
        switch (loginType) {
            case LOGIN_FAILED -> {
                userLoginRetryHandler.handleFailedAttempts(user.getUserPrincipal().getEmail());
            }
            case LOGIN_SUCCESS -> {
                userLoginRetryHandler.handleSuccessFulAttempt(user.getUserPrincipal().getEmail());
                userRepository.setUserLastLogin(user);
            }
        }
    }

    public boolean isUserExists(String email) {
        return !(patientRepository.existsByUserPrincipal_Email(email) &&
                consultantRepository.existsByUserPrincipal_Email(email) &&
                adminRepository.existsByUserPrincipal_Email(email));
    }
    public boolean isUserExistsByUserId(String userId) {
        return patientRepository.existsByUserId(userId) ||
                consultantRepository.existsByUserId(userId) ||
                adminRepository.existsByUserId(userId);
    }



}






















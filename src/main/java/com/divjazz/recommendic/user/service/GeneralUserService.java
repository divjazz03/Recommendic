package com.divjazz.recommendic.user.service;

import com.divjazz.recommendic.security.ApiAuthentication;
import com.divjazz.recommendic.user.domain.RequestContext;
import com.divjazz.recommendic.user.enums.LoginType;
import com.divjazz.recommendic.user.exception.UserNotFoundException;
import com.divjazz.recommendic.user.model.Admin;
import com.divjazz.recommendic.user.model.Consultant;
import com.divjazz.recommendic.user.model.Patient;
import com.divjazz.recommendic.user.model.User;
import com.divjazz.recommendic.user.model.userAttributes.credential.UserCredential;
import com.divjazz.recommendic.user.repository.AdminRepository;
import com.divjazz.recommendic.user.repository.ConsultantRepository;
import com.divjazz.recommendic.user.repository.PatientRepository;
import com.divjazz.recommendic.user.repository.UserRepository;
import com.divjazz.recommendic.user.repository.projection.UserSecurityProjection;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GeneralUserService {

    private final UserLoginRetryHandler userLoginRetryHandler;

    private final ObjectMapper objectMapper;
    private final PatientRepository patientRepository;
    private final ConsultantRepository consultantRepository;
    private final AdminRepository adminRepository;
    private final UserRepository userRepository;


    public User retrieveUserByEmail(String email) {
        return findUserByEmail(email);
    }

    public UserSecurityProjection retrieveUserDetailInfoByEmail(String email) {
        var result = userRepository.findByEmail_Security_Projection(email)
                .orElseThrow(UserNotFoundException::new);
        var credential = objectMapper.convertValue(result.userCredential(), UserCredential.class);

        return new UserSecurityProjection(result.id(), result.email(), result.userId(), credential);

    }

    public UserCredential retrieveUserCredentials(String email) {
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
        return findUserByUserId(id);
    }

    @Transactional
    public void enableUser(String userId) {
        User user = findUserByUserId(userId);
        user.setEnabled(true);
        switch (user.getUserType()) {
            case CONSULTANT -> consultantRepository.save((Consultant) user);
            case PATIENT -> patientRepository.save((Patient) user);
            case ADMIN -> adminRepository.save((Admin) user);
        }
    }

    private User findUserByEmail(String email) {
        Optional<Patient> patient = patientRepository.findByEmail(email);
        Optional<Consultant> consultant = consultantRepository.findByEmail(email);
        Optional<Admin> admin = adminRepository.findByEmail(email);
        if (patient.isPresent()){
            return patient.get();
        }
        if (consultant.isPresent()) {
            return consultant.get();
        }
        if (admin.isPresent()) {
            return admin.get();
        }

        throw new UserNotFoundException();
    }

    private User findUserByUserId(String userId) {
        Optional<Patient> patient = patientRepository.findByUserId(userId);
        Optional<Consultant> consultant = consultantRepository.findByUserId(userId);
        Optional<Admin> admin = adminRepository.findByUserId(userId);
        if (patient.isPresent()){
            return patient.get();
        }
        if (consultant.isPresent()) {
            return consultant.get();
        }
        if (admin.isPresent()) {
            return admin.get();
        }

        throw new UserNotFoundException();
    }

    @Transactional
    public void updateLoginAttempt(User user, LoginType loginType) throws UserNotFoundException {
        RequestContext.setUserId(user.getId());
        switch (loginType) {
            case LOGIN_FAILED -> {
                userLoginRetryHandler.handleFailedAttempts(user.getEmail());
            }
            case LOGIN_SUCCESS -> {
                userLoginRetryHandler.handleSuccessFulAttempt(user.getEmail());
                user.setLastLogin(LocalDateTime.now());
                switch (user.getUserType()) {
                    case CONSULTANT -> consultantRepository.save((Consultant) user);
                    case PATIENT -> patientRepository.save((Patient) user);
                    case ADMIN -> adminRepository.save((Admin) user);
                }
            }
        }
    }

    public boolean isUserNotExists(String email) {
        return !patientRepository.existsByEmail(email) &&
                !consultantRepository.existsByEmail(email) &&
                !adminRepository.existsByEmail(email);
    }

}






















package com.divjazz.recommendic.user.service;

import com.divjazz.recommendic.global.exception.EntityNotFoundException;
import com.divjazz.recommendic.security.SessionUser;
import com.divjazz.recommendic.security.exception.AuthenticationException;
import com.divjazz.recommendic.user.controller.UserController;
import com.divjazz.recommendic.user.domain.RequestContext;
import com.divjazz.recommendic.user.controller.consultant.payload.ConsultantProfileResponse;
import com.divjazz.recommendic.user.controller.patient.payload.PatientProfileResponse;
import com.divjazz.recommendic.user.enums.LoginType;
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
import java.util.HashMap;
import java.util.Map;
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
                .orElseThrow(() -> new EntityNotFoundException("User with email: %s not found".formatted(email)));
        var credential = objectMapper.convertValue(result.userCredential(), UserCredential.class);

        return new UserSecurityProjection(result.id(), result.email(), result.userId(), credential);

    }

    public UserCredential retrieveUserCredentials(String email) {
        return objectMapper
                .convertValue(userRepository
                                .findByEmail_ReturningCredentialsJsonB(email)
                        .orElseThrow(() -> new EntityNotFoundException("User with email: %s not found".formatted(email))),
                        UserCredential.class);
    }


    public Map<String, Object> retrieveCurrentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() == null || authentication.getPrincipal().equals("anonymousUser")) {
            throw new AuthenticationException("No authentication found in context please login");
        }
        var sessionUser = (SessionUser) authentication.getPrincipal();

        User user =  retrieveUserByEmail(sessionUser.getEmail());
        Map<String, Object> response = new HashMap<>();
        switch (user) {
            case Consultant consultant -> {

                response.put("user", new UserController.CurrentUser(
                        consultant.getUserId(),
                        consultant.getUserPrincipal().getRole().getName(),
                        consultant.getUserType(),
                        consultant.getUserStage()
                ));
                response.put("profile",
                        new ConsultantProfileResponse(
                                consultant.getProfile().getUserName(),
                                consultant.getProfile().getPhoneNumber(),
                                consultant.getProfile().getAddress(),
                                consultant.getProfile().getProfilePicture()
                        ));
            }
            case Patient patient -> {
                response.put("user", new UserController.CurrentUser(
                        patient.getUserId(),
                        patient.getUserPrincipal().getRole().getName(),
                        patient.getUserType(),
                        patient.getUserStage()
                ));
                response.put("profile",
                        new PatientProfileResponse(
                                patient.getPatientProfile().getUserName(),
                                patient.getPatientProfile().getPhoneNumber(),
                                patient.getPatientProfile().getAddress(),
                                patient.getPatientProfile().getProfilePicture()
                        ));
            }
            default -> throw new IllegalStateException("Unexpected value: " + user);
        }
        return response;
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
        Optional<Consultant> consultant = consultantRepository.findByUserPrincipal_Email(email);
        Optional<Admin> admin = adminRepository.findByUserPrincipal_Email(email);
        if (patient.isPresent()){
            return patient.get();
        }
        if (consultant.isPresent()) {
            return consultant.get();
        }
        if (admin.isPresent()) {
            return admin.get();
        }

        throw new EntityNotFoundException("User with email: %s not found".formatted(email));
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

        throw new EntityNotFoundException("User with id: %s not found".formatted(userId));
    }

    @Transactional
    public void updateLoginAttempt(User user, LoginType loginType) throws EntityNotFoundException {
        RequestContext.setUserId(user.getId());
        switch (loginType) {
            case LOGIN_FAILED -> {
                userLoginRetryHandler.handleFailedAttempts(user.getUserPrincipal().getUsername());
            }
            case LOGIN_SUCCESS -> {
                userLoginRetryHandler.handleSuccessFulAttempt(user.getUserPrincipal().getUsername());
                user.setLastLogin(LocalDateTime.now());
                switch (user.getUserType()) {
                    case CONSULTANT -> consultantRepository.save((Consultant) user);
                    case PATIENT -> patientRepository.save((Patient) user);
                    case ADMIN -> adminRepository.save((Admin) user);
                }
            }
        }
    }

    public boolean isUserExists(String email) {
        return patientRepository.existsByUserPrincipal_Email(email) ||
                consultantRepository.existsByUserPrincipal_Email(email) ||
                adminRepository.existsByUserPrincipal_Email(email) ;
    }
    public boolean isUserExistsByUserId(String userId) {
        return patientRepository.existsByUserId(userId) ||
                consultantRepository.existsByUserId(userId) ||
                adminRepository.existsByUserId(userId);
    }

}






















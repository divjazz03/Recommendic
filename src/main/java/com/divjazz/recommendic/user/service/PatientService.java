package com.divjazz.recommendic.user.service;

import com.divjazz.recommendic.exception.EntityNotFoundException;
import com.divjazz.recommendic.user.domain.RequestContext;
import com.divjazz.recommendic.user.dto.PatientDTO;
import com.divjazz.recommendic.user.dto.PatientInfoResponse;
import com.divjazz.recommendic.user.enums.EventType;
import com.divjazz.recommendic.user.enums.MedicalCategoryEnum;
import com.divjazz.recommendic.user.enums.UserStage;
import com.divjazz.recommendic.user.enums.UserType;
import com.divjazz.recommendic.user.event.UserEvent;
import com.divjazz.recommendic.user.exception.UserAlreadyExistsException;
import com.divjazz.recommendic.user.model.Patient;
import com.divjazz.recommendic.user.model.userAttributes.Role;
import com.divjazz.recommendic.user.model.UserConfirmation;
import com.divjazz.recommendic.user.model.userAttributes.credential.UserCredential;
import com.divjazz.recommendic.user.repository.PatientRepository;
import com.divjazz.recommendic.user.repository.confirmation.UserConfirmationRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final Logger log = LoggerFactory.getLogger(PatientService.class);
    private final UserConfirmationRepository userConfirmationRepository;
    private final GeneralUserService userService;
    private final PasswordEncoder encoder;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final PatientRepository patientRepository;

    private final TransactionTemplate transactionTemplate;



    public PatientInfoResponse createPatient(PatientDTO patientDTO) {
        UserCredential userCredential = new UserCredential(encoder.encode(patientDTO.password()));
        Patient user = new Patient(
                patientDTO.userName(),
                patientDTO.email(),
                patientDTO.phoneNumber(),
                patientDTO.gender(),
                patientDTO.address(),
                Role.PATIENT,
                userCredential);
        user.setUserCredential(userCredential);
        user.setUserType(UserType.PATIENT);
        user.setUserStage(UserStage.ONBOARDING);

        if (userService.isUserNotExists(user.getEmail())) {
            RequestContext.setUserId(user.getId());
            var userConfirmation = new UserConfirmation(user);
            transactionTemplate.executeWithoutResult( status -> {
                try {
                    patientRepository.save(user);
                    userConfirmationRepository.save(userConfirmation);
                } catch (IllegalArgumentException e) {
                    status.setRollbackOnly();
                }
            });
            UserEvent userEvent = new UserEvent(user, EventType.REGISTRATION, Map.of("key", userConfirmation.getKey()));
            applicationEventPublisher.publishEvent(userEvent);
            log.info("New user with id {} created", user.getUserId());
            return new PatientInfoResponse(user.getUserId()
                    , user.getUserNameObject().getLastName()
                    , user.getUserNameObject().getFirstName()
                    , user.getPhoneNumber()
                    , user.getGender().toString()
                    , user.getAddress());
        } else {
            throw new UserAlreadyExistsException(user.getEmail());
        }
    }

    @Transactional(readOnly = true)
    public Page<Patient> getAllPatients(Pageable pageable) {
        return patientRepository.findAll(pageable);
    }

    public void deletePatientByUserId(String patient_Id) {
        patientRepository.deleteByUserId(patient_Id);
    }



    @Transactional(readOnly = true)
    public Patient findPatientByUserId(String id) {
        return patientRepository.findByUserId(id)
                .orElseThrow(() -> new EntityNotFoundException("Patient with id: %s not found".formatted(id)));
    }

    @Transactional
    public boolean handleOnboarding(String userId, List<String> medicalCategories) {
            Set<String> medicalCategorySet = medicalCategories.stream()
                    .map(MedicalCategoryEnum::fromValue)
                    .map(MedicalCategoryEnum::getValue)
                    .collect(Collectors.toSet());
            Patient patient = patientRepository.findByUserId(userId)
                    .orElseThrow(() -> new EntityNotFoundException("Patient with id: %s not found".formatted(userId)));
            patient.setMedicalCategories(medicalCategorySet.toArray(String[]::new));
            patient.setUserStage(UserStage.ACTIVE_USER);
        return true;
    }

}

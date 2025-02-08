package com.divjazz.recommendic.user.service;

import com.divjazz.recommendic.consultation.model.Consultation;
import com.divjazz.recommendic.user.domain.RequestContext;
import com.divjazz.recommendic.user.dto.PatientDTO;
import com.divjazz.recommendic.user.dto.PatientInfoResponse;
import com.divjazz.recommendic.user.enums.EventType;
import com.divjazz.recommendic.user.enums.MedicalCategory;
import com.divjazz.recommendic.user.enums.UserType;
import com.divjazz.recommendic.user.event.UserEvent;
import com.divjazz.recommendic.user.exception.UserAlreadyExistsException;
import com.divjazz.recommendic.user.exception.UserNotFoundException;
import com.divjazz.recommendic.user.model.Patient;
import com.divjazz.recommendic.user.model.userAttributes.ProfilePicture;
import com.divjazz.recommendic.user.model.userAttributes.Role;
import com.divjazz.recommendic.user.model.userAttributes.confirmation.UserConfirmation;
import com.divjazz.recommendic.user.model.userAttributes.credential.UserCredential;
import com.divjazz.recommendic.user.repository.PatientRepository;
import com.divjazz.recommendic.user.repository.RoleRepository;
import com.divjazz.recommendic.user.repository.UserRepository;
import com.divjazz.recommendic.user.repository.confirmation.UserConfirmationRepository;
import com.divjazz.recommendic.user.repository.credential.UserCredentialRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PatientService {

    private final Logger log = LoggerFactory.getLogger(PatientService.class);
    public static final int DEFAULT_PAGE_SIZE = 50;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserCredentialRepository userCredentialRepository;
    private final UserConfirmationRepository userConfirmationRepository;
    private final GeneralUserService userService;
    private final PasswordEncoder encoder;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final PatientRepository patientRepository;

    private final TransactionTemplate transactionTemplate;


    public PatientService(
            PatientRepository patientRepository,
            UserRepository userRepository, RoleRepository roleRepository, UserCredentialRepository userCredentialRepository,
            UserConfirmationRepository userConfirmationRepository, GeneralUserService userService,
            PasswordEncoder encoder,
            ApplicationEventPublisher applicationEventPublisher, TransactionTemplate transactionTemplate) {
        this.userRepository = userRepository;
        this.patientRepository = patientRepository;
        this.roleRepository = roleRepository;
        this.userCredentialRepository = userCredentialRepository;
        this.userConfirmationRepository = userConfirmationRepository;
        this.userService = userService;
        this.encoder = encoder;
        this.applicationEventPublisher = applicationEventPublisher;
        this.transactionTemplate = transactionTemplate;
    }



    public PatientInfoResponse createPatient(PatientDTO patientDTO) {
        Role role = roleRepository.getRoleByName("ROLE_PATIENT").orElseThrow(() -> new RuntimeException("No such role found"));
        UserCredential userCredential = new UserCredential(encoder.encode(patientDTO.password()));
        Patient user = new Patient(
                patientDTO.userName(),
                patientDTO.email(),
                patientDTO.phoneNumber(),
                patientDTO.gender(),
                patientDTO.address(),
                role,
                userCredential);
        user.setUserCredential(userCredential);
        user.setUserType(UserType.PATIENT);
        userCredential.setUser(user);

        if (userService.isUserNotExists(user.getEmail())) {
            RequestContext.setUserId(user.getId());
            var userConfirmation = new UserConfirmation(user);
            transactionTemplate.execute( status -> {
                try {

                    userRepository.save(user);
                    userCredentialRepository.save(userCredential);
                    userConfirmationRepository.save(userConfirmation);
                } catch (Exception e) {
                    status.setRollbackOnly();
                }
                return null;

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

    @Transactional
    public void deletePatientByUserId(String patient_Id) {
        patientRepository.deleteByUserId(patient_Id);
    }

    @Transactional
    public void modifyPatient(Patient patient) {
        patientRepository.save(patient);
    }

    @Transactional(readOnly = true)
    public Patient findPatientById(long id) {
        return patientRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public Patient findPatientByUserId(String id) {
        return patientRepository.findByUserId(id).orElseThrow(UserNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public Set<Patient> findPatientsByMedicalCategories(Set<MedicalCategory> medicalCategories) {
        return patientRepository.findPatientByMedicalCategories(medicalCategories);
    }
    public Set<Consultation> findAllConsultationForaGivenPatient(String patientId) {
        return patientRepository.findAllConsultationByPatientId(patientId);
    }

    public Page<Consultation> getConsultations(String patient_id, int page) {
        var pageable = PageRequest.of(page, DEFAULT_PAGE_SIZE);
        return patientRepository.findConsultationsByPatientIdOrderByCreatedAtAsc(patient_id, pageable);
    }

}

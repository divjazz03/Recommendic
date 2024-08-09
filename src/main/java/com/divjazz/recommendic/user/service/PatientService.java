package com.divjazz.recommendic.user.service;

import com.divjazz.recommendic.user.domain.RequestContext;
import com.divjazz.recommendic.user.dto.PatientInfoResponse;
import com.divjazz.recommendic.user.dto.PatientDTO;
import com.divjazz.recommendic.user.enums.EventType;
import com.divjazz.recommendic.user.enums.MedicalCategory;
import com.divjazz.recommendic.user.event.UserEvent;
import com.divjazz.recommendic.user.exceptions.UserAlreadyExistsException;
import com.divjazz.recommendic.user.exceptions.UserNotFoundException;
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
import com.google.common.collect.ImmutableSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class PatientService {

    private Logger log = LoggerFactory.getLogger(PatientService.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserCredentialRepository userCredentialRepository;
    private final UserConfirmationRepository userConfirmationRepository;
    private final GeneralUserService userService;
    private final PasswordEncoder encoder;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final PatientRepository patientRepository;




    public PatientService(
            PatientRepository patientRepository,
            UserRepository userRepository, RoleRepository roleRepository, UserCredentialRepository userCredentialRepository,
            UserConfirmationRepository userConfirmationRepository, GeneralUserService userService,
            PasswordEncoder encoder,
            ApplicationEventPublisher applicationEventPublisher) {
        this.userRepository = userRepository;
        this.patientRepository = patientRepository;
        this.roleRepository = roleRepository;
        this.userCredentialRepository = userCredentialRepository;
        this.userConfirmationRepository = userConfirmationRepository;
        this.userService = userService;
        this.encoder = encoder;
        this.applicationEventPublisher = applicationEventPublisher;
    }



    @Transactional
    public PatientInfoResponse createPatient(PatientDTO patientDTO){
        Role role = roleRepository.getRoleByName("ROLE_PATIENT").orElseThrow(() -> new RuntimeException("No such role found"));
        log.info("The patient role is {}", role);
        UserCredential userCredential = new UserCredential(encoder.encode(patientDTO.password()));
        Patient user = new Patient(
                patientDTO.userName(),
                patientDTO.email(),
                patientDTO.phoneNumber(),
                patientDTO.gender(),
                patientDTO.address(),
                role,
                userCredential);
        Set<MedicalCategory> medicalCategories = Arrays.stream(patientDTO.categoryOfInterest())
                .map(MedicalCategory::valueOf)
                .collect(Collectors.toSet());
        var profilePicture = new ProfilePicture();

        profilePicture.setPictureUrl("https://cdn-icons-png.flaticon.com/512/149/149071.png");
        profilePicture.setName("149071.png");
        user.setProfilePicture(profilePicture);
        user.setMedicalCategories(medicalCategories);
        user.setUserCredential(userCredential);
        userCredential.setUser(user);
        if (userService.isUserNotExists(user.getEmail())) {
            RequestContext.setUserId(user.getId());
            var userConfirmation = new UserConfirmation(user);
            userRepository.save(user);
            userCredentialRepository.save(userCredential);
            userConfirmationRepository.save(userConfirmation);
            UserEvent userEvent = new UserEvent(user, EventType.REGISTRATION,Map.of("key",userConfirmation.getKey()));
            applicationEventPublisher.publishEvent(userEvent);
            return new PatientInfoResponse(user.getId()
                    ,user.getUserNameObject().getLastName()
                    ,user.getUserNameObject().getFirstName()
                    ,user.getPhoneNumber()
                    ,user.getGender().toString()
                    ,user.getAddress());
        } else {
            throw new UserAlreadyExistsException(user.getEmail());
        }
    }
    @Transactional(readOnly = true)
    public Set<Patient> getAllPatients(){
        return ImmutableSet
                .copyOf(patientRepository.findAll());
    }

    public void deletePatientById(long patient_Id){
        patientRepository.deleteById(patient_Id);
    }

    public void modifyPatient(Patient patient){
        patientRepository.save(patient);
    }
    @Transactional(readOnly = true)
    public Patient findPatientById(long id){
        return patientRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);
    }

}

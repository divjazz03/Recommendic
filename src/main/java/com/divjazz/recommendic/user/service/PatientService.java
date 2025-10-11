package com.divjazz.recommendic.user.service;

import com.divjazz.recommendic.global.exception.EntityNotFoundException;
import com.divjazz.recommendic.global.general.PageResponse;
import com.divjazz.recommendic.recommendation.model.ConsultantRecommendation;
import com.divjazz.recommendic.recommendation.service.RecommendationService;
import com.divjazz.recommendic.security.utils.AuthUtils;
import com.divjazz.recommendic.user.controller.patient.payload.*;
import com.divjazz.recommendic.user.controller.patient.payload.ConsultantRecommendationResponse.ConsultantMinimal;
import com.divjazz.recommendic.user.enums.EventType;
import com.divjazz.recommendic.user.enums.Gender;
import com.divjazz.recommendic.user.enums.UserStage;
import com.divjazz.recommendic.user.event.UserEvent;
import com.divjazz.recommendic.user.exception.UserAlreadyExistsException;
import com.divjazz.recommendic.user.model.MedicalCategoryEntity;
import com.divjazz.recommendic.user.model.Patient;
import com.divjazz.recommendic.user.model.UserConfirmation;
import com.divjazz.recommendic.user.model.userAttributes.PatientProfile;
import com.divjazz.recommendic.user.model.userAttributes.ProfilePicture;
import com.divjazz.recommendic.user.model.userAttributes.Role;
import com.divjazz.recommendic.user.model.userAttributes.UserName;
import com.divjazz.recommendic.user.model.userAttributes.credential.UserCredential;
import com.divjazz.recommendic.user.repository.PatientRepository;
import com.divjazz.recommendic.user.repository.confirmation.UserConfirmationRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PatientService {

    public static final String PATIENT_ROLE_NAME = "ROLE_PATIENT";

    private final Logger log = LoggerFactory.getLogger(PatientService.class);
    private final UserConfirmationRepository userConfirmationRepository;
    private final GeneralUserService userService;
    private final PasswordEncoder encoder;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final PatientRepository patientRepository;
    private final RecommendationService recommendationService;
    private final ConsultantService consultantService;
    private final AuthUtils authUtils;
    private final RoleService roleService;
    private final MedicalCategoryService medicalCategoryService;

    @Transactional
    public PatientInfoResponse createPatient(PatientRegistrationParams patientRegistrationParams) {

        if (!userService.isUserExists(patientRegistrationParams.email())) {
            UserCredential userCredential = new UserCredential(encoder.encode(patientRegistrationParams.password()));
            Role role = roleService.getRoleByName(PATIENT_ROLE_NAME);
            Patient user = new Patient(
                    patientRegistrationParams.email(),
                    Gender.valueOf(patientRegistrationParams.gender().toUpperCase()),
                    userCredential, role);
            user.setUserStage(UserStage.ONBOARDING);
            var profilePicture = new ProfilePicture();

            profilePicture.setPictureUrl("https://cdn-icons-png.flaticon.com/512/149/149071.png");
            profilePicture.setName("149071.png");

            PatientProfile patientProfile = PatientProfile.builder()
                    .patient(user)
                    .dateOfBirth(LocalDate.parse(patientRegistrationParams.dateOfBirth()))
                    .userName(new UserName(patientRegistrationParams.firstName(), patientRegistrationParams.lastName()))
                    .profilePicture(profilePicture)
                    .patient(user)
                    .build();
            user.setPatientProfile(patientProfile);

            var savedPatient = patientRepository.save(user);
            var userConfirmation = new UserConfirmation(savedPatient);
            userConfirmationRepository.save(userConfirmation);
            UserEvent userEvent = new UserEvent(user.getUserType(),
                    EventType.REGISTRATION,
                    Map.of("key", userConfirmation.getKey(),
                            "email", user.getUserPrincipal().getUsername(),
                            "firstname", patientProfile.getUserName().getFirstName()));
            applicationEventPublisher.publishEvent(userEvent);
            recommendationService.createConsultantRecommendationForPatient(savedPatient);
            log.info("New user with id {} created", user.getUserId());
            return new PatientInfoResponse(
                    user.getUserId(),
                    patientProfile.getUserName().getLastName(),
                    patientProfile.getUserName().getFirstName(),
                    patientProfile.getAge(),
                    user.getGender().toString(),
                    patientProfile.getAddress());
        } else {
            throw new UserAlreadyExistsException(patientRegistrationParams.email());
        }
    }

    @Transactional(readOnly = true)
    public PageResponse<PatientInfoResponse> getAllPatients(Pageable pageable) {
        return PageResponse.from(patientRepository.findAll(pageable).map(this::toPatientInfoResponse));
    }

    @Transactional(readOnly = true)
    public PatientInfoResponse getPatientDetailById(String userId) {
        return toPatientInfoResponse(findPatientByUserId(userId));
    }

    @Transactional
    public void deletePatientByUserId(String patient_Id) {
        if (!userService.isUserExistsByUserId(patient_Id)) {
            throw new EntityNotFoundException("Patient does not exist or has already been deleted");
        }
        patientRepository.deleteByUserId(patient_Id);
    }


    @Transactional(readOnly = true)
    public Patient findPatientByUserId(String id) {
        return patientRepository.findByUserId(id)
                .orElseThrow(() -> new EntityNotFoundException("Patient with id: %s not found".formatted(id)));
    }

    @Transactional
    public void handleOnboarding(String userId, List<String> medicalCategoryNames) {
        Patient patient = patientRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Patient with id: %s not found".formatted(userId)));
        Set<MedicalCategoryEntity> medicalCategoryEntities = medicalCategoryService.getAllByNames(medicalCategoryNames);
        if (patient.getUserStage() == UserStage.ONBOARDING) {
            patient.setMedicalCategories(medicalCategoryEntities);
            patient.setUserStage(UserStage.ACTIVE_USER);
        }
    }

    @Transactional(readOnly = true)
    public ConsultantRecommendationResponse getRecommendationForPatient() {
        Patient patient = (Patient) authUtils.getCurrentUser();
        Set<ConsultantMinimal> recommendations = recommendationService
                .retrieveRecommendationByPatient(patient)
                .stream()
                .map(ConsultantRecommendation::getConsultant)
                .map(consultantService::getConsultantRecommendationProfile)
                .collect(Collectors.toSet());
        return new ConsultantRecommendationResponse(recommendations);
    }
    @Transactional
    public PatientProfileDetails getMyProfileDetails() {
        Patient patient = (Patient) authUtils.getCurrentUser();
        PatientProfile patientProfile = patient.getPatientProfile();
        String[] interests = patient.getMedicalCategories()
                .stream()
                .map(MedicalCategoryEntity::getName)
                .toArray(String[]::new);

        var patientProfileFull = new PatientProfileFull(
                patientProfile.getUserName(),
                patient.getUserPrincipal().getUsername(),
                patientProfile.getPhoneNumber(),
                patientProfile.getDateOfBirth().toString(),
                patient.getGender().name().toLowerCase(),
                patientProfile.getAddress(),
                interests
        );
        return new PatientProfileDetails(patientProfileFull);
    }

    private PatientInfoResponse toPatientInfoResponse(Patient patient) {
        return new PatientInfoResponse(
                patient.getUserId(),
                patient.getPatientProfile().getUserName().getLastName(),
                patient.getPatientProfile().getUserName().getFirstName(),
                patient.getPatientProfile().getAge(),
                patient.getGender().toString(),
                patient.getPatientProfile().getAddress());
    }

    public PatientProfileResponse getThisPatientProfile() {
        var currentUser = ((Patient) authUtils.getCurrentUser()).getPatientProfile();

        return new PatientProfileResponse(
                currentUser.getUserName(),
                currentUser.getAge(),
                currentUser.getAddress(),
                currentUser.getProfilePicture()
        );

    }
}

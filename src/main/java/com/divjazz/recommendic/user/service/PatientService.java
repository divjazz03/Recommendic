package com.divjazz.recommendic.user.service;

import com.divjazz.recommendic.global.exception.EntityNotFoundException;
import com.divjazz.recommendic.global.general.PageResponse;
import com.divjazz.recommendic.notification.app.service.AppNotificationService;
import com.divjazz.recommendic.recommendation.model.ConsultantRecommendation;
import com.divjazz.recommendic.recommendation.service.RecommendationService;
import com.divjazz.recommendic.security.service.SecurityService;
import com.divjazz.recommendic.security.utils.AuthUtils;
import com.divjazz.recommendic.user.controller.patient.payload.*;
import com.divjazz.recommendic.user.dto.ConsultantFull;
import com.divjazz.recommendic.user.dto.ConsultantMinimal;
import com.divjazz.recommendic.user.dto.UserDTO;
import com.divjazz.recommendic.user.enums.EventType;
import com.divjazz.recommendic.user.enums.Gender;
import com.divjazz.recommendic.user.enums.UserStage;
import com.divjazz.recommendic.user.event.UserEvent;
import com.divjazz.recommendic.user.exception.UserAlreadyExistsException;
import com.divjazz.recommendic.user.model.*;
import com.divjazz.recommendic.user.model.userAttributes.*;
import com.divjazz.recommendic.user.model.userAttributes.credential.UserCredential;
import com.divjazz.recommendic.user.repository.PatientCustomRepository;
import com.divjazz.recommendic.user.repository.PatientProfileRepository;
import com.divjazz.recommendic.user.repository.PatientRepository;
import com.divjazz.recommendic.user.repository.confirmation.UserConfirmationRepository;
import com.divjazz.recommendic.user.repository.projection.MedicalCategoryProjection;
import com.divjazz.recommendic.user.repository.projection.PatientProfileProjection;
import com.divjazz.recommendic.user.repository.projection.UserProjection;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDate;
import java.util.*;
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
    private final PatientCustomRepository patientCustomRepository;
    private final AppNotificationService appNotificationService;
    private final SecurityService securityService;

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
            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronization() {
                        @Override
                        public void afterCommit() {
                            recommendationService.createConsultantRecommendationForPatient(savedPatient);
                        }
                    }
            );
            appNotificationService.createNotificationSetting(savedPatient);
            securityService.createUserSetting(savedPatient);
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
    public void handleOnboarding(String userId, Set<String> medicalCategoryNames) {
        Set<MedicalCategoryEntity> medicalCategoryEntities = medicalCategoryService.getAllByNames(medicalCategoryNames);
        Patient patient = patientRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Patient with id: %s not found".formatted(userId)));
        if (patient.getUserStage() == UserStage.ONBOARDING) {
            patient.setMedicalCategories(medicalCategoryEntities);
            patient.setUserStage(UserStage.ACTIVE_USER);
        }
    }

    @Transactional(readOnly = true)
    public Page<ConsultantMinimal> getRecommendationForPatient(Pageable pageable) {
        UserDTO userDTO =  authUtils.getCurrentUser();
        return recommendationService.retrieveRecommendationByPatient(userDTO.userId(), pageable)
                .map(ConsultantRecommendation::getConsultant)
                .map(consultantService::getConsultantRecommendationProfileMinimal);
    }

    @Transactional(readOnly = true)
    public PatientProfileDetails getMyProfileDetails() {
        UserDTO userDTO = authUtils.getCurrentUser();
        var patientProfileProjectionOpt = patientCustomRepository.getFullPatientProfileByUserId(userDTO.userId());
        if (patientProfileProjectionOpt.isPresent()) {
            var profileProjection = patientProfileProjectionOpt.get();
            return new PatientProfileDetails(
                    profileProjection.getUserName(),
                    profileProjection.getEmail(),
                    profileProjection.getPhoneNumber(),
                    Objects.nonNull(profileProjection.getDateOfBirth()) ? profileProjection.getDateOfBirth().toString(): null,
                    userDTO.gender().name().toLowerCase(),
                    profileProjection.getAddress(),
                    profileProjection.getMedicalCategories().stream().map(MedicalCategoryProjection::name).collect(Collectors.toSet()),
                    profileProjection.getProfilePicture().getPictureUrl()

            );
        }

        throw new EntityNotFoundException("User profile not found");
    }

    public PatientFullConsultantView getFullConsultantView(String consultantId) {
        ConsultantFull fullConsultantDetails = consultantService.getFullConsultantDetails(consultantId);

        return PatientFullConsultantView.builder()
                .id(fullConsultantDetails.id())
                .bio(fullConsultantDetails.bio())
                .availableSlots(fullConsultantDetails.availableSlots())
                .educations(fullConsultantDetails.educations())
                .experience(fullConsultantDetails.experience())
                .fee(fullConsultantDetails.fee().inPerson())
                .image(fullConsultantDetails.image())
                .languages(fullConsultantDetails.languages())
                .location(fullConsultantDetails.location())
                .rating(fullConsultantDetails.rating())
                .reviews(fullConsultantDetails.reviews())
                .specializations(fullConsultantDetails.specializations())
                .stats(fullConsultantDetails.stats())
                .title(fullConsultantDetails.title())
                .totalReviews(fullConsultantDetails.totalReviews())
                .name(fullConsultantDetails.name())
                .profileImgUrl(fullConsultantDetails.profileImgUrl())
                .build();
    }

    @Transactional
    public PatientProfileDetails updatePatientProfileDetails(PatientProfileUpdateRequest updateRequest) {
        var userDTO = authUtils.getCurrentUser();

        Patient patient = patientRepository.findByUserId(userDTO.userId())
                .orElseThrow(() -> new EntityNotFoundException("Patient not found"));

        if (Objects.nonNull(updateRequest)) {
            if (Objects.nonNull(updateRequest.address())) {
                Address addressToChange = getAddressToChange(updateRequest, patient);
                patient.getPatientProfile().setAddress(addressToChange);

            }
            if (Objects.nonNull(updateRequest.dateOfBirth())) {
                patient.getPatientProfile().setDateOfBirth(LocalDate.parse(updateRequest.dateOfBirth()));
            }
            if (Objects.nonNull(updateRequest.phoneNumber())) {
                patient.getPatientProfile().setPhoneNumber(updateRequest.phoneNumber());
            }
            if (Objects.nonNull(updateRequest.interests())) {
                Set<MedicalCategoryEntity> medicalCategoryToAdd = medicalCategoryService.getAllByNames(updateRequest.interests());
                patient.setMedicalCategories(medicalCategoryToAdd);
            }
            if (Objects.nonNull(updateRequest.userName())) {
                var userNameToChange = patient.getPatientProfile().getUserName();
                if (Objects.nonNull(updateRequest.userName().getFirstName())) {
                    userNameToChange.setFirstName(updateRequest.userName().getFirstName());
                }
                if (Objects.nonNull(updateRequest.userName().getLastName())) {
                    userNameToChange.setLastName(updateRequest.userName().getLastName());
                }
                patient.getPatientProfile().setUserName(userNameToChange);

            }
            if (Objects.nonNull(updateRequest.profileImgUrl())) {
                patient.getPatientProfile().getProfilePicture().setPictureUrl(updateRequest.profileImgUrl());
            }

            patient = patientRepository.save(patient);
        }
        return new PatientProfileDetails(
                patient.getPatientProfile().getUserName(),
                patient.getUserPrincipal().getUsername(),
                patient.getPatientProfile().getPhoneNumber(),
                patient.getPatientProfile().getDateOfBirth().toString(),
                patient.getGender().name().toLowerCase(),
                patient.getPatientProfile().getAddress(),
                patient.getMedicalCategories().stream().map(MedicalCategoryEntity::getName).collect(Collectors.toSet()),
                patient.getPatientProfile().getProfilePicture().getPictureUrl()
        );
    }

    private static Address getAddressToChange(PatientProfileUpdateRequest updateRequest, Patient patient) {
        Address addressToChange = patient.getPatientProfile().getAddress();
        if (Objects.isNull(addressToChange)) {
            addressToChange = new Address();
        }
        if (Objects.nonNull(updateRequest.address().getCity())) {
            addressToChange.setCity(updateRequest.address().getCity());
        }
        if (Objects.nonNull(updateRequest.address().getCountry())) {
            addressToChange.setCountry(updateRequest.address().getCountry());
        }
        if (Objects.nonNull(updateRequest.address().getState())) {
            addressToChange.setState(updateRequest.address().getState());
        }
        return addressToChange;
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
        var currentUser = authUtils.getCurrentUser();
        var profileOpt = patientCustomRepository.getFullPatientProfileByUserId(currentUser.userId());
        if (profileOpt.isPresent()) {
            var profile = profileOpt.get();
            return new PatientProfileResponse(
                    profile.getUserName(),
                    PatientProfile.getAge(profile.getDateOfBirth()),
                    profile.getAddress(),
                    profile.getProfilePicture()
            );
        }
        throw new EntityNotFoundException("No profile for this user");
    }
}

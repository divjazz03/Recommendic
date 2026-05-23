package com.divjazz.recommendic.user.service;

import com.divjazz.recommendic.global.exception.EntityNotFoundException;
import com.divjazz.recommendic.global.general.PageResponse;
import com.divjazz.recommendic.recommendation.service.RecommendationService;
import com.divjazz.recommendic.security.utils.AuthUtils;
import com.divjazz.recommendic.user.controller.patient.payload.*;
import com.divjazz.recommendic.user.dto.ConsultantFull;
import com.divjazz.recommendic.user.dto.ConsultantMinimal;
import com.divjazz.recommendic.user.dto.PatientMedicalData;
import com.divjazz.recommendic.user.dto.UserDTO;
import com.divjazz.recommendic.user.enums.EventType;
import com.divjazz.recommendic.user.enums.Gender;
import com.divjazz.recommendic.user.enums.UserStage;
import com.divjazz.recommendic.user.event.UserEvent;
import com.divjazz.recommendic.user.exception.UserAlreadyExistsException;
import com.divjazz.recommendic.user.mapper.PatientMapper;
import com.divjazz.recommendic.user.model.MedicalCategoryEntity;
import com.divjazz.recommendic.user.model.Patient;
import com.divjazz.recommendic.user.model.UserConfirmation;
import com.divjazz.recommendic.user.model.userAttributes.*;
import com.divjazz.recommendic.user.model.userAttributes.credential.UserCredential;
import com.divjazz.recommendic.user.model.userAttributes.preferences.PatientNotificationPreference;
import com.divjazz.recommendic.user.model.userAttributes.preferences.UserSecuritySetting;
import com.divjazz.recommendic.user.repository.PatientCustomRepository;
import com.divjazz.recommendic.user.repository.PatientRepository;
import com.divjazz.recommendic.user.repository.confirmation.UserConfirmationRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
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
    private final PatientCustomRepository patientCustomRepository;
    private final PatientMapper patientMapper;
    @Value("${spring.session.timeout}")
    private Duration sessionDuration;

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

    @Transactional
    public PatientInfoResponse createPatient(PatientRegistrationParams patientRegistrationParams) {

        if (userService.isUserExists(patientRegistrationParams.email())) {
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

            var patientProfile = PatientProfile.builder()
                    .patient(user)
                    .dateOfBirth(LocalDate.parse(patientRegistrationParams.dateOfBirth()))
                    .userName(new UserName(patientRegistrationParams.firstName(), patientRegistrationParams.lastName()))
                    .profilePicture(profilePicture)
                    .patient(user)
                    .build();
            user.setPatientProfile(patientProfile);
            var userSecuritySetting = new UserSecuritySetting(
                    false, sessionDuration.toMinutes(), true

            );
            user.setPatientSecuritySetting(userSecuritySetting);
            var notificationPreference = PatientNotificationPreference.builder()
                    .emailNotificationEnabled(true)
                    .smsNotificationEnabled(false)
                    .appointmentRemindersEnabled(true)
                    .labResultsUpdateEnabled(true)
                    .systemUpdatesEnabled(true)
                    .marketingEmailEnabled(false)
                    .build();
            user.setNotificationPreference(notificationPreference);

            var savedPatient = patientRepository.save(user);
            var userConfirmation = new UserConfirmation(savedPatient);
            userConfirmationRepository.save(userConfirmation);
            UserEvent userEvent = new UserEvent(user.getUserType(),
                    EventType.REGISTRATION,
                    Map.of("key", userConfirmation.getKey(),
                            "email", user.getUserPrincipal().getUsername(),
                            "firstname", patientProfile.getUserName().getFirstName()));
            applicationEventPublisher.publishEvent(userEvent);
            log.info("New user with id {} created", user.getUserId());
            return patientMapper.toInfoResponse(savedPatient);
        } else {
            throw new UserAlreadyExistsException(patientRegistrationParams.email());
        }
    }

    @Transactional(readOnly = true)
    public PageResponse<PatientInfoResponse> getAllPatients(Pageable pageable) {
        return PageResponse.from(patientRepository.findAll(pageable).map(patientMapper::toInfoResponse));
    }

    @Transactional(readOnly = true)
    public PatientInfoResponse getPatientDetailById(String userId) {
        return patientMapper.toInfoResponse(findPatientByUserId(userId));
    }

    @Transactional
    public void deletePatientByUserId(String patient_Id) {
        if (userService.isUserExistsByUserId(patient_Id)) {
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
    public void handleOnboarding(String userId, PatientOnboardingRequest request) {

        Patient patient = patientRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Patient with id: %s not found".formatted(userId)));
        if (patient.getUserStage() == UserStage.ONBOARDING) {
            if (Objects.nonNull(request.specializations()) && !request.specializations().isEmpty()) {
                Set<MedicalCategoryEntity> medicalCategoryEntities = medicalCategoryService.getAllByIds(request.specializations());
                if (medicalCategoryEntities.isEmpty()) {
                    var medicalCategoriesString = String.join(", ", request.specializations());
                    throw new IllegalArgumentException("Invalid medical categories, [%s]".formatted(medicalCategoriesString));
                }
                patient.setMedicalCategories(medicalCategoryEntities);
            }
            if ((Objects.nonNull(request.alcoholConsumption()) && !request.alcoholConsumption().isBlank())
                    || (Objects.nonNull(request.smokingStatus()) && !request.smokingStatus().isBlank())
                    || (Objects.nonNull(request.exerciseFrequency()) && !request.exerciseFrequency().isBlank())
                    || (Objects.nonNull(request.dietaryRestrictions()) && !request.dietaryRestrictions().isBlank())) {
                LifeStyleInfo lifeStyleInfo = new LifeStyleInfo(
                        request.smokingStatus(),
                        request.alcoholConsumption(),
                        request.exerciseFrequency(),
                        request.dietaryRestrictions()
                );
                patient.getPatientProfile().setLifeStyleInfo(lifeStyleInfo);
            }
            if ((Objects.nonNull(request.bloodType()))
                    || (Objects.nonNull(request.chronicConditions()) && !request.chronicConditions().isBlank())
                    || (Objects.nonNull(request.allergies()) && !request.allergies().isBlank())
                    || (Objects.nonNull(request.currentMedications()) && !request.currentMedications().isBlank())
                    || (Objects.nonNull(request.pastSurgeries()) && !request.pastSurgeries().isBlank())
                    || (Objects.nonNull(request.familyHistory()) && !request.familyHistory().isBlank())
            ) {
                MedicalHistory medicalHistory = new MedicalHistory(
                        request.allergies(),
                        request.chronicConditions(),
                        request.pastSurgeries(),
                        request.familyHistory(),
                        request.currentMedications()
                );
                patient.getPatientProfile().setMedicalHistory(medicalHistory);
            }
            if (Objects.nonNull(request.dateOfBirth()) && !request.dateOfBirth().isBlank()) {
                try {
                    var localDate = LocalDate.parse(request.dateOfBirth());
                    patient.getPatientProfile().setDateOfBirth(localDate);
                } catch (DateTimeParseException ex) {
                    throw new IllegalArgumentException(ex.getMessage());
                }
            }
            if (Objects.nonNull(request.phone()) && !request.phone().isBlank()) {
                patient.getPatientProfile().setPhoneNumber(request.phone());
            }
            if (Objects.nonNull(request.emergencyPhone()) && !request.emergencyPhone().isBlank()) {
                patient.getPatientProfile().setEmergencyContactNumber(request.emergencyPhone());
            }
            if (Objects.nonNull(request.emergencyContact()) && !request.emergencyContact().isBlank()) {
                patient.getPatientProfile().setEmergencyContactName(request.emergencyContact());
            }
            if (Objects.nonNull(request.bloodType())) {
                patient.getPatientProfile().setBloodType(request.bloodType());
            }
            patient.setUserStage(UserStage.ACTIVE_USER);
        }

    }

    @Transactional(readOnly = true)
    public PageResponse<ConsultantMinimal> getRecommendationForPatient(Pageable pageable) {
        UserDTO userDTO = authUtils.getCurrentUser();
        var consultants = consultantService.getAllConsultantsMinimal(pageable);
        return PageResponse.fromSet(pageable, consultants.elements(), consultants.total());
    }

    @Transactional(readOnly = true)
    public PatientProfileDetails getMyProfileDetails() {
        UserDTO userDTO = authUtils.getCurrentUser();
        var patient = patientRepository.findByUserId(userDTO.userId()).orElseThrow(() -> new EntityNotFoundException("User not found"));
        return patientMapper.toPatientProfileDetails(patient);
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
                Set<MedicalCategoryEntity> medicalCategoryToAdd = medicalCategoryService.getAllByIds(
                        updateRequest.interests().stream().map(String::toLowerCase).collect(Collectors.toSet())
                );
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
            if (Objects.nonNull(updateRequest.notificationPreference())) {
                var requestNotificationPreference = updateRequest.notificationPreference();
                var notificationPreferenceChanges = PatientNotificationPreference.builder()
                        .marketingEmailEnabled(requestNotificationPreference.marketingEmailEnabled())
                        .emailNotificationEnabled(requestNotificationPreference.marketingEmailEnabled())
                        .labResultsUpdateEnabled(requestNotificationPreference.labResultsUpdateEnabled())
                        .smsNotificationEnabled(requestNotificationPreference.smsNotificationEnabled())
                        .appointmentRemindersEnabled(requestNotificationPreference.appointmentRemindersEnabled())
                        .build();
                patient.setNotificationPreference(notificationPreferenceChanges);
            }
            if (Objects.nonNull(updateRequest.securityPreference())) {
                var requestSecurityPreference = updateRequest.securityPreference();
                var securityPreferenceChanges = UserSecuritySetting.builder()
                        .loginAlertsEnabled(requestSecurityPreference.loginAlertsEnabled())
                        .multiFactorAuthEnabled(requestSecurityPreference.multiFactorAuthEnabled())
                        .sessionTimeoutMin(requestSecurityPreference.sessionTimeoutMin())
                        .build();
                patient.setPatientSecuritySetting(securityPreferenceChanges);
            }

            patient = patientRepository.save(patient);
        }
        return patientMapper.toPatientProfileDetails(patient);
    }

    public PatientProfileResponse getThisPatientProfile() {
        var currentUser = authUtils.getCurrentUser();
        var profileOpt = patientCustomRepository.getFullPatientProfileByUserId(currentUser.userId());
        if (profileOpt.isPresent()) {
            var profile = profileOpt.get();
            return new PatientProfileResponse(
                    profile.userName(),
                    PatientProfile.getAge(profile.dateOfBirth()),
                    profile.address(),
                    profile.profilePicture()
            );
        }
        throw new EntityNotFoundException("No profile for this user");
    }

    public PatientMedicalData getMedicalData(String patientId) {
        return patientCustomRepository.getPatientMedicalDataById(patientId)
                .orElseThrow(() -> new EntityNotFoundException("Medical Data for this patient not found"));
    }
}

package com.divjazz.recommendic.user.service;

import com.divjazz.recommendic.global.exception.EntityNotFoundException;
import com.divjazz.recommendic.global.general.PageResponse;
import com.divjazz.recommendic.recommendation.model.ConsultantRecommendation;
import com.divjazz.recommendic.recommendation.service.RecommendationService;
import com.divjazz.recommendic.user.dto.PatientDTO;
import com.divjazz.recommendic.user.dto.PatientInfoResponse;
import com.divjazz.recommendic.user.enums.EventType;
import com.divjazz.recommendic.user.enums.MedicalCategoryEnum;
import com.divjazz.recommendic.user.enums.UserStage;
import com.divjazz.recommendic.user.event.UserEvent;
import com.divjazz.recommendic.user.exception.UserAlreadyExistsException;
import com.divjazz.recommendic.user.model.Patient;
import com.divjazz.recommendic.user.model.UserConfirmation;
import com.divjazz.recommendic.user.model.userAttributes.PatientProfile;
import com.divjazz.recommendic.user.model.userAttributes.ProfilePicture;
import com.divjazz.recommendic.user.model.userAttributes.credential.UserCredential;
import com.divjazz.recommendic.user.repository.PatientProfileRepository;
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
    private final RecommendationService recommendationService;

    @Transactional
    public PatientInfoResponse createPatient(PatientDTO patientDTO) {

        if (!userService.isUserExists(patientDTO.email())) {
            UserCredential userCredential = new UserCredential(encoder.encode(patientDTO.password()));
            Patient user = new Patient(
                    patientDTO.email(),
                    patientDTO.gender(),
                    userCredential);
            user.setUserStage(UserStage.ONBOARDING);
            var profilePicture = new ProfilePicture();

            profilePicture.setPictureUrl("https://cdn-icons-png.flaticon.com/512/149/149071.png");
            profilePicture.setName("149071.png");


            var userConfirmation = new UserConfirmation(user);

            PatientProfile patientProfile = PatientProfile.builder()
                    .address(patientDTO.address())
                    .patient(user)
                    .phoneNumber(patientDTO.phoneNumber())
                    .userName(patientDTO.userName())
                    .profilePicture(profilePicture)
                    .patient(user)
                    .build();
            user.setPatientProfile(patientProfile);

            var savedPatient = patientRepository.save(user);
            userConfirmationRepository.save(userConfirmation);
            UserEvent userEvent = new UserEvent(user.getUserType(),
                    EventType.REGISTRATION,
                    Map.of("key", userConfirmation.getKey(),
                            "email", user.getEmail(),
                            "firstname", patientProfile.getUserName().getFirstName()));
            applicationEventPublisher.publishEvent(userEvent);
            recommendationService.createConsultantRecommendationForPatient(savedPatient);
            log.info("New user with id {} created", user.getUserId());
            return new PatientInfoResponse(
                    user.getUserId(),
                    patientProfile.getUserName().getLastName(),
                    patientProfile.getUserName().getFirstName(),
                    patientProfile.getPhoneNumber(),
                    user.getGender().toString(),
                    patientProfile.getAddress());
        } else {
            throw new UserAlreadyExistsException(patientDTO.email());
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
    public void handleOnboarding(String userId, List<String> medicalCategories) {
        Set<String> medicalCategorySet = medicalCategories.stream()
                .map(MedicalCategoryEnum::fromValue)
                .map(MedicalCategoryEnum::getValue)
                .collect(Collectors.toSet());
        Patient patient = patientRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Patient with id: %s not found".formatted(userId)));
        if (patient.getUserStage() == UserStage.ONBOARDING) {
            patient.setMedicalCategories(medicalCategorySet.toArray(String[]::new));
            patient.setUserStage(UserStage.ACTIVE_USER);
        }
    }
    @Transactional(readOnly = true)
    public Set<ConsultantRecommendation> getRecommendationForPatient(String userId) {
        Patient patient = findPatientByUserId(userId);
        return recommendationService.retrieveRecommendationByPatient(patient);
    }
    private PatientInfoResponse toPatientInfoResponse(Patient patient) {
       return new PatientInfoResponse(
                patient.getUserId(),
                patient.getPatientProfile().getUserName().getLastName(),
                patient.getPatientProfile().getUserName().getFirstName(),
                patient.getPatientProfile().getPhoneNumber(),
                patient.getGender().toString(),
                patient.getPatientProfile().getAddress());
    }

}

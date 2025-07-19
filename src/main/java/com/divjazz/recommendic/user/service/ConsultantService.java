package com.divjazz.recommendic.user.service;

import com.divjazz.recommendic.global.exception.EntityNotFoundException;
import com.divjazz.recommendic.global.general.PageResponse;
import com.divjazz.recommendic.user.controller.consultant.ConsultantRegistrationParams;
import com.divjazz.recommendic.user.domain.MedicalCategory;
import com.divjazz.recommendic.user.domain.RequestContext;
import com.divjazz.recommendic.user.dto.ConsultantDTO;
import com.divjazz.recommendic.user.dto.ConsultantInfoResponse;
import com.divjazz.recommendic.user.enums.EventType;
import com.divjazz.recommendic.user.enums.Gender;
import com.divjazz.recommendic.user.enums.MedicalCategoryEnum;
import com.divjazz.recommendic.user.enums.UserStage;
import com.divjazz.recommendic.user.event.UserEvent;
import com.divjazz.recommendic.user.exception.UserAlreadyExistsException;
import com.divjazz.recommendic.user.model.Consultant;
import com.divjazz.recommendic.user.model.userAttributes.Address;
import com.divjazz.recommendic.user.model.userAttributes.ConsultantProfile;
import com.divjazz.recommendic.user.model.userAttributes.ProfilePicture;
import com.divjazz.recommendic.user.model.UserConfirmation;
import com.divjazz.recommendic.user.model.userAttributes.UserName;
import com.divjazz.recommendic.user.model.userAttributes.credential.UserCredential;
import com.divjazz.recommendic.user.repository.ConsultantProfileRepository;
import com.divjazz.recommendic.user.repository.ConsultantRepository;
import com.divjazz.recommendic.user.repository.confirmation.UserConfirmationRepository;
import com.divjazz.recommendic.user.repository.projection.ConsultantInfoProjection;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableSet;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ConsultantService {

    private final UserConfirmationRepository userConfirmationRepository;
    private final PasswordEncoder passwordEncoder;
    private final GeneralUserService userService;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final ConsultantRepository consultantRepository;
    private final ConsultantProfileRepository consultantProfileRepository;
    private final ObjectMapper objectMapper;


    @Transactional
    public ConsultantInfoResponse createConsultant(ConsultantRegistrationParams consultantRegistrationParams) {

        if (!userService.isUserExists(consultantRegistrationParams.email())) {
            UserCredential userCredential = new UserCredential(passwordEncoder.encode(consultantRegistrationParams.password()));
            Consultant user = new Consultant(
                    consultantRegistrationParams.email(),
                    Gender.valueOf(consultantRegistrationParams.gender().toUpperCase(Locale.ENGLISH)),
                    userCredential);
            user.setCertified(false);
            user.setUserStage(UserStage.ONBOARDING);
            var profilePicture = new ProfilePicture();

            profilePicture.setPictureUrl("https://cdn-icons-png.flaticon.com/512/149/149071.png");
            profilePicture.setName("149071.png");


            var userConfirmation = new UserConfirmation(user);
            ConsultantProfile consultantProfile = ConsultantProfile
                    .builder()
                    .consultant(user)
                    .address(new Address(
                            consultantRegistrationParams.city(),
                            consultantRegistrationParams.state(),
                            consultantRegistrationParams.country()))
                    .phoneNumber(consultantRegistrationParams.phoneNumber())
                    .profilePicture(profilePicture)
                    .userName(new UserName(
                            consultantRegistrationParams.firstName(),
                            consultantRegistrationParams.lastName()
                    ))
                    .build();
            user.setProfile(consultantProfile);
            var savedConsultant = consultantRepository.save(user);
            UserEvent userEvent = new UserEvent(user.getUserType(),
                    EventType.REGISTRATION,
                    Map.of("key", userConfirmation.getKey(),
                            "email", user.getEmail(),
                            "firstname", consultantProfile.getUserName().getFirstName()));
            applicationEventPublisher.publishEvent(userEvent);
            return new ConsultantInfoResponse(
                    savedConsultant.getUserId(),
                    savedConsultant.getProfile().getUserName().getLastName(),
                    savedConsultant.getProfile().getUserName().getFirstName(),
                    savedConsultant.getGender().toString(),
                    savedConsultant.getProfile().getPhoneNumber(),
                   savedConsultant.getProfile().getAddress(),
                    null
            );
        } else {
            throw new UserAlreadyExistsException(consultantRegistrationParams.email());
        }
    }

    @Transactional(readOnly = true)
    public PageResponse<ConsultantInfoResponse> getAllConsultants(Pageable pageable) {
        return PageResponse.from(
                consultantRepository.findAll(pageable).map(this::toConsultantInfoResponse)
        );
    }

    @Transactional(readOnly = true)
    public Set<Consultant> getConsultantsByCategory(MedicalCategoryEnum category) {
        return ImmutableSet.
                copyOf(
                        consultantRepository
                                .findByMedicalCategoryIgnoreCase(
                                        MedicalCategory.fromMedicalCategoryEnum(category).name()
                                )

                );
    }

    public Set<ConsultantInfoResponse> searchSomeConsultantsByQuery(String query) {
        return consultantRepository.findConsultantInfo(query)
                .stream()
                .map(this::toConsultantInfoResponse)
                .collect(Collectors.toSet());
    }

    public ConsultantInfoResponse toConsultantInfoResponse(Consultant consultant) {
        return new ConsultantInfoResponse(
                consultant.getUserId(),
                consultant.getProfile().getUserName().getLastName(),
                consultant.getProfile().getUserName().getFirstName(),
                consultant.getGender().toString(),
                consultant.getProfile().getPhoneNumber(),
                consultant.getProfile().getAddress(),
                consultant.getMedicalCategory() == null ? null : consultant.getMedicalCategory().name()
        );
    }

    public ConsultantInfoResponse toConsultantInfoResponse(ConsultantInfoProjection consultantInfoProjection) {
        return new ConsultantInfoResponse(
                consultantInfoProjection.consultantId(),
                consultantInfoProjection.lastName(),
                consultantInfoProjection.firstName(),
                consultantInfoProjection.gender(),
                consultantInfoProjection.phoneNumber(),
                objectMapper.convertValue(consultantInfoProjection.address(), Address.class),
                consultantInfoProjection.medicalSpecialization()
        );
    }

    public ConsultantProfile getConsultantProfile(Consultant consultant) {
        return consultantProfileRepository.findById(consultant.getId())
                .orElseThrow(() -> new EntityNotFoundException("Profile not found"));
    }

    public Set<Consultant> getConsultantsByMedicalSpecialization(MedicalCategoryEnum medicalCategoryEnum) {
        return consultantRepository.findByMedicalCategoryIgnoreCase(medicalCategoryEnum.getValue());
    }

    @Transactional
    public void deleteConsultantById(String userId) {
        if (!userService.isUserExistsByUserId(userId)) {
            throw new EntityNotFoundException("Consultant does not exist or has been deleted");
        }
        consultantRepository.deleteByUserId(userId);
    }


    public Set<Consultant> getAllUnCertifiedConsultants() {
        return consultantRepository.findUnCertifiedConsultant();
    }

    public boolean handleOnboarding(String userId, String medicalSpecialization) {

        MedicalCategoryEnum medicalSpec = MedicalCategoryEnum.fromValue(medicalSpecialization);
        Consultant consultant = consultantRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Consultant with id: %s not found".formatted(userId)));
        consultant.setMedicalCategory(medicalSpec);
        consultant.setUserStage(UserStage.ACTIVE_USER);
        consultantRepository.save(consultant);

        return true;
    }

    public ConsultantInfoResponse getConsultantByUserId(String userId) {
        return consultantRepository.findByUserId(userId)
                .map(this::toConsultantInfoResponse)
                .orElseThrow(() -> new EntityNotFoundException("Consultant with id: %s not found".formatted(userId)));
    }
}

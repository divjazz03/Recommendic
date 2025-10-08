package com.divjazz.recommendic.user.service;

import com.divjazz.recommendic.global.exception.EntityNotFoundException;
import com.divjazz.recommendic.global.general.PageResponse;
import com.divjazz.recommendic.security.utils.AuthUtils;
import com.divjazz.recommendic.user.controller.consultant.ConsultantRegistrationParams;
import com.divjazz.recommendic.user.domain.MedicalCategory;
import com.divjazz.recommendic.user.dto.ConsultantInfoResponse;
import com.divjazz.recommendic.user.dto.ConsultantProfileResponse;
import com.divjazz.recommendic.user.enums.EventType;
import com.divjazz.recommendic.user.enums.Gender;
import com.divjazz.recommendic.user.enums.UserStage;
import com.divjazz.recommendic.user.event.UserEvent;
import com.divjazz.recommendic.user.exception.UserAlreadyExistsException;
import com.divjazz.recommendic.user.model.Consultant;
import com.divjazz.recommendic.user.model.MedicalCategoryEntity;
import com.divjazz.recommendic.user.model.userAttributes.*;
import com.divjazz.recommendic.user.model.UserConfirmation;
import com.divjazz.recommendic.user.model.userAttributes.credential.UserCredential;
import com.divjazz.recommendic.user.repository.ConsultantProfileRepository;
import com.divjazz.recommendic.user.repository.ConsultantRepository;
import com.divjazz.recommendic.user.repository.confirmation.UserConfirmationRepository;
import com.divjazz.recommendic.user.repository.projection.ConsultantInfoProjection;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableSet;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ConsultantService {
    public static final String CONSULTANT_ROLE_NAME = "ROLE_CONSULTANT";

    private final UserConfirmationRepository userConfirmationRepository;
    private final PasswordEncoder passwordEncoder;
    private final GeneralUserService userService;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final ConsultantRepository consultantRepository;
    private final ConsultantProfileRepository consultantProfileRepository;
    private final ObjectMapper objectMapper;
    private final AuthUtils authUtils;
    private final RoleService roleService;
    private final MedicalCategoryService medicalCategoryService;


    @Transactional
    public ConsultantInfoResponse createConsultant(ConsultantRegistrationParams consultantRegistrationParams) {

        if (!userService.isUserExists(consultantRegistrationParams.email())) {
            UserCredential userCredential = new UserCredential(passwordEncoder.encode(consultantRegistrationParams.password()));
            Role role = roleService.getRoleByName(CONSULTANT_ROLE_NAME);
            Consultant user = new Consultant(
                    consultantRegistrationParams.email(),
                    Gender.valueOf(consultantRegistrationParams.gender().toUpperCase(Locale.ENGLISH)),
                    userCredential, role);
            user.setCertified(false);
            user.setUserStage(UserStage.ONBOARDING);
            var profilePicture = new ProfilePicture();

            profilePicture.setPictureUrl("https://cdn-icons-png.flaticon.com/512/149/149071.png");
            profilePicture.setName("149071.png");



            ConsultantProfile consultantProfile = ConsultantProfile
                    .builder()
                    .consultant(user)
                    .dateOfBirth(LocalDate.parse(consultantRegistrationParams.dateOfBirth()))
                    .profilePicture(profilePicture)
                    .userName(new UserName(
                            consultantRegistrationParams.firstName(),
                            consultantRegistrationParams.lastName()
                    ))
                    .build();
            user.setProfile(consultantProfile);
            var savedConsultant = consultantRepository.save(user);
            var userConfirmation = new UserConfirmation(savedConsultant);
            userConfirmationRepository.save(userConfirmation);
            UserEvent userEvent = new UserEvent(user.getUserType(),
                    EventType.REGISTRATION,
                    Map.of("key", userConfirmation.getKey(),
                            "email", user.getUserPrincipal().getUsername(),
                            "firstname", consultantProfile.getUserName().getFirstName()));
            applicationEventPublisher.publishEvent(userEvent);
            return new ConsultantInfoResponse(
                    savedConsultant.getUserId(),
                    savedConsultant.getProfile().getUserName().getLastName(),
                    savedConsultant.getProfile().getUserName().getFirstName(),
                    savedConsultant.getGender().toString(),
                    savedConsultant.getProfile().getAge(),
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
    public Set<Consultant> getConsultantsByCategory(MedicalCategoryEntity category) {
        return ImmutableSet.
                copyOf(
                        consultantRepository
                                .findBySpecialization(
                                        category
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
                consultant.getProfile().getAge(),
                consultant.getProfile().getAddress(),
                consultant.getSpecialization() == null ? null : consultant.getSpecialization().getName()
        );
    }

    public ConsultantInfoResponse toConsultantInfoResponse(ConsultantInfoProjection consultantInfoProjection) {
        return new ConsultantInfoResponse(
                consultantInfoProjection.consultantId(),
                consultantInfoProjection.lastName(),
                consultantInfoProjection.firstName(),
                consultantInfoProjection.gender(),
                consultantInfoProjection.age(),
                objectMapper.convertValue(consultantInfoProjection.address(), Address.class),
                consultantInfoProjection.medicalSpecialization()
        );
    }

    public ConsultantProfile getConsultantProfile(Consultant consultant) {
        return consultantProfileRepository.findById(consultant.getId())
                .orElseThrow(() -> new EntityNotFoundException("Profile not found"));
    }
    public ConsultantProfile getConsultantProfileByConsultantId(String id) {
        return consultantProfileRepository.findByConsultantId(id)
                .orElseThrow(() -> new EntityNotFoundException("Consultant this consultant has no profile"));
    }
    public ConsultantProfileResponse getConsultantProfileResponse() {
        var currentUser = (Consultant) authUtils.getCurrentUser();

        return new ConsultantProfileResponse(
                currentUser.getProfile().getUserName(),
                currentUser.getProfile().getPhoneNumber(),
                currentUser.getProfile().getAddress(),
                currentUser.getProfile().getProfilePicture()
        );
    }

    public Set<Consultant> getConsultantsByMedicalSpecialization(MedicalCategoryEntity medicalCategory) {
        return consultantRepository.findBySpecialization(medicalCategory);
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

        MedicalCategoryEntity medicalCategory = medicalCategoryService.getMedicalCategoryByName(medicalSpecialization);
        Consultant consultant = consultantRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Consultant with id: %s not found".formatted(userId)));
        consultant.setSpecialization(medicalCategory);
        consultant.setUserStage(UserStage.ACTIVE_USER);
        consultantRepository.save(consultant);

        return true;
    }

    public ConsultantInfoResponse getConsultantInfoByUserId(String userId) {
        return consultantRepository.findByUserId(userId)
                .map(this::toConsultantInfoResponse)
                .orElseThrow(() -> new EntityNotFoundException("Consultant with id: %s not found".formatted(userId)));
    }

}

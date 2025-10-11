package com.divjazz.recommendic.user.service;

import com.divjazz.recommendic.appointment.controller.payload.ConsultationFee;
import com.divjazz.recommendic.global.exception.EntityNotFoundException;
import com.divjazz.recommendic.global.general.PageResponse;
import com.divjazz.recommendic.security.utils.AuthUtils;
import com.divjazz.recommendic.user.controller.consultant.payload.*;
import com.divjazz.recommendic.user.controller.patient.payload.ConsultantEducationResponse;
import com.divjazz.recommendic.user.controller.patient.payload.ConsultantRecommendationResponse;
import com.divjazz.recommendic.user.enums.EventType;
import com.divjazz.recommendic.user.enums.Gender;
import com.divjazz.recommendic.user.enums.UserStage;
import com.divjazz.recommendic.user.event.UserEvent;
import com.divjazz.recommendic.user.exception.UserAlreadyExistsException;
import com.divjazz.recommendic.user.model.Consultant;
import com.divjazz.recommendic.user.model.MedicalCategoryEntity;
import com.divjazz.recommendic.user.model.certification.ConsultantEducation;
import com.divjazz.recommendic.user.model.userAttributes.*;
import com.divjazz.recommendic.user.model.UserConfirmation;
import com.divjazz.recommendic.user.model.userAttributes.credential.UserCredential;
import com.divjazz.recommendic.user.repository.ConsultantProfileRepository;
import com.divjazz.recommendic.user.repository.ConsultantRepository;
import com.divjazz.recommendic.user.repository.ConsultantStatRepository;
import com.divjazz.recommendic.user.repository.certificationRepo.ConsultantEducationRepository;
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
import java.util.*;
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
    private final ConsultantStatRepository consultantStatRepository;
    private final ConsultantEducationRepository consultantEducationRepository;


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

    public List<Consultant> getAllConsultants() {
        return consultantRepository.findAll();
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

    public ConsultantRecommendationResponse.ConsultantMinimal getConsultantRecommendationProfile(Consultant consultant) {
        ConsultantProfile consultantProfile = consultant.getProfile();
        ConsultantStat consultantStat = consultantStatRepository
                .findConsultantStatByConsultantId(consultant.getUserId()).orElse(ConsultantStat.ofEmpty());
        List<ConsultantEducation> consultantEducations = consultantEducationRepository.findAllByConsultant(consultant);

        return new ConsultantRecommendationResponse.ConsultantMinimal(
                consultant.getUserId(),
                consultantProfile.getUserName().getFullName(),
                consultant.getSpecialization().getName(),
                consultantStat.getRating(),0,
                consultantProfile.getYearsOfExperience(),
                consultantProfile.getLocationOfInstitution(),
                "",
                new ConsultationFee(200,300),
                consultantProfile.getProfilePicture().getPictureUrl(),
                consultantEducations.stream()
                        .map(ConsultantEducation::getDegree)
                        .collect(Collectors.toList()),
                Arrays.stream(consultantProfile.getLanguages()).toList(),
                ""
        );
    }

    public ConsultantProfileDetails getConsultantProfileDetails() {
        Consultant consultant = (Consultant) authUtils.getCurrentUser();
        ConsultantEducation consultantEducation;
        try{
            consultantEducation = consultantEducationRepository.findAllByConsultant(consultant).getFirst();
        } catch (NoSuchElementException e) {
            consultantEducation = ConsultantEducation.ofEmpty();
        }
        var consultantProfile = ConsultantProfileFull.builder()
                .address(consultant.getProfile().getAddress())
                .bio(consultant.getProfile().getBio())
                .dateOfBirth(consultant.getProfile().getDateOfBirth().toString())
                .email(consultant.getUserPrincipal().getUsername())
                .location(consultant.getProfile().getLocationOfInstitution())
                .experience(String.valueOf(consultant.getProfile().getYearsOfExperience()))
                .gender(consultant.getGender().name().toLowerCase())
                .languages(consultant.getProfile().getLanguages())
                .specialty(consultant.getSpecialization().getName())
                .userName(consultant.getProfile().getUserName())
                .phoneNumber(consultant.getProfile().getPhoneNumber())
                .build();
        return new ConsultantProfileDetails(
                consultantProfile,
                new ConsultantEducationResponse(
                        String.valueOf(consultantEducation.getYear()),
                        consultantEducation.getInstitution(),
                        consultantEducation.getDegree()
                )
        );
    }
    @Transactional
    public ConsultantProfileDetails updateConsultantProfileDetails(ConsultantProfileUpdateRequest consultantProfileUpdateRequest) {
        Consultant consultant = (Consultant) authUtils.getCurrentUser();
        ConsultantEducation consultantEducation;

        try {
            consultantEducation = consultantEducationRepository.findAllByConsultant(consultant).getFirst();
        } catch (NoSuchElementException ex) {
            consultantEducation = ConsultantEducation.ofEmpty();
        }
        ConsultantProfileFull profile = consultantProfileUpdateRequest.profile();
        if (Objects.nonNull(profile)) {
            if (Objects.nonNull(profile.specialty())) {
                consultant.setSpecialization(medicalCategoryService.getMedicalCategoryByName(profile.specialty()));
            }

            if (Objects.nonNull(profile.address())) {
                consultant.getProfile().setAddress(profile.address());
            }

            if (Objects.nonNull(profile.bio())) {
                consultant.getProfile().setBio(profile.bio());
            }

            if (Objects.nonNull(profile.experience())) {
                consultant.getProfile().setYearsOfExperience(Integer.parseInt(profile.experience()));
            }

            if (Objects.nonNull(profile.languages())) {
                consultant.getProfile().setLanguages(profile.languages());
            }

            if (Objects.nonNull(profile.location())) {
                consultant.getProfile().setLocationOfInstitution(profile.location());
            }

            if (Objects.nonNull(profile.phoneNumber())) {
                consultant.getProfile().setPhoneNumber(profile.phoneNumber());
            }

            consultant = consultantRepository.save(consultant);
        }
        var consultantEducationRequest = consultantProfileUpdateRequest.education();
        if (Objects.nonNull(consultantEducationRequest)) {
            if (Objects.nonNull(consultantEducationRequest.degree())) {
                consultantEducation.setDegree(consultantEducationRequest.degree());
            }
            if (Objects.nonNull(consultantEducationRequest.institution())) {
                consultantEducation.setInstitution(consultantEducationRequest.institution());
            }
            if (Objects.nonNull(consultantEducationRequest.year())) {
                consultantEducation.setYear(Integer.parseInt(consultantEducationRequest.year()));
            }
            consultantEducation = consultantEducationRepository.save(consultantEducation);
        }

        var consultantProfile = ConsultantProfileFull.builder()
                .address(consultant.getProfile().getAddress())
                .bio(consultant.getProfile().getBio())
                .dateOfBirth(consultant.getProfile().getDateOfBirth().toString())
                .email(consultant.getUserPrincipal().getUsername())
                .location(consultant.getProfile().getLocationOfInstitution())
                .experience(String.valueOf(consultant.getProfile().getYearsOfExperience()))
                .gender(consultant.getGender().name().toLowerCase())
                .languages(consultant.getProfile().getLanguages())
                .specialty(consultant.getSpecialization().getName())
                .userName(consultant.getProfile().getUserName())
                .phoneNumber(consultant.getProfile().getPhoneNumber())
                .build();


        return new ConsultantProfileDetails(
                consultantProfile,
                new ConsultantEducationResponse(
                        String.valueOf(consultantEducation.getYear()),
                        consultantEducation.getInstitution(),
                        consultantEducation.getDegree()
                )
        );

    }

}

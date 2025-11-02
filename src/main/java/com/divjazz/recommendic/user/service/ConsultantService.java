package com.divjazz.recommendic.user.service;

import com.divjazz.recommendic.appointment.controller.payload.ConsultationFee;
import com.divjazz.recommendic.appointment.service.AppointmentService;
import com.divjazz.recommendic.consultation.service.ConsultationService;
import com.divjazz.recommendic.global.exception.EntityNotFoundException;
import com.divjazz.recommendic.global.general.PageResponse;
import com.divjazz.recommendic.notification.app.model.AppNotification;
import com.divjazz.recommendic.notification.app.service.AppNotificationService;
import com.divjazz.recommendic.security.service.SecurityService;
import com.divjazz.recommendic.security.utils.AuthUtils;
import com.divjazz.recommendic.user.controller.consultant.payload.*;
import com.divjazz.recommendic.user.controller.patient.payload.ConsultantEducationResponse;
import com.divjazz.recommendic.user.dto.*;
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
import com.divjazz.recommendic.user.repository.ConsultantCustomRepository;
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
import java.time.OffsetDateTime;
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
    private final AppointmentService appointmentService;
    private final ConsultationService consultationService;
    private final ConsultantCustomRepository consultantCustomRepository;
    private final AppNotificationService appNotificationService;
    private final SecurityService securityService;


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
            appNotificationService.createNotificationSetting(savedConsultant);
            securityService.createUserSetting(savedConsultant);
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
        var currentUser = authUtils.getCurrentUser();
        var consultantProfileOpt = consultantProfileRepository.findByConsultantId(currentUser.userId());
        if (consultantProfileOpt.isPresent()) {
            var consultantProfile = consultantProfileOpt.get();
            return new ConsultantProfileResponse(
                    consultantProfile.getUserName(),
                    consultantProfile.getPhoneNumber(),
                    consultantProfile.getAddress(),
                    consultantProfile.getProfilePicture()
            );
        }
        return null;

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

    public ConsultantMinimal getConsultantRecommendationProfileMinimal(Consultant consultant) {
        ConsultantProfile consultantProfile = consultant.getProfile();
        Set<ConsultantEducation> consultantEducations = consultantEducationRepository.findAllByConsultant(consultant);

        return new ConsultantMinimal(
                consultant.getUserId(),
                consultantProfile.getUserName().getFullName(),
                consultant.getSpecialization().getName(),
                4.5,0,
                consultantProfile.getYearsOfExperience(),
                consultantProfile.getLocationOfInstitution(),
                "",
                new ConsultationFee(200,300),
                consultantProfile.getProfilePicture().getPictureUrl(),
                consultantEducations.stream()
                        .map(ConsultantEducation::getDegree)
                        .collect(Collectors.toList()),
                Arrays.stream(consultantProfile.getLanguages() == null? new String[0]: consultantProfile.getLanguages()).toList(),
                ""
        );
    }
    public ConsultantFull getFullConsultantDetails(String consultantId) {
        Consultant consultant = consultantRepository.findByUserId(consultantId)
                .orElseThrow(() -> new EntityNotFoundException("No consultant of this id found"));
        var consultantProfile = consultant.getProfile();
        ConsultantStat consultantStat = consultantStatRepository.findConsultantStatByConsultantId(consultantId)
                .orElse(ConsultantStat.ofEmpty());
        Set<ConsultantEducation> consultantEducations = consultantEducationRepository.findAllByConsultant(consultant);

        var availableSlots =  appointmentService.getTodayAvailableSlots(consultantId)
                .stream()
                .map(OffsetDateTime::toString)
                .collect(Collectors.toSet());
        return ConsultantFull.builder()
                .bio(consultantProfile.getBio())
                .experience(consultantProfile.getYearsOfExperience())
                .fee(new ConsultationFee(20,50))
                .id(consultant.getUserId())
                .image(consultantProfile.getProfilePicture().getPictureUrl())
                .location(consultantProfile.getLocationOfInstitution())
                .name(consultantProfile.getUserName().getFullName())
                .rating(4.5)
                .title(consultantProfile.getTitle())
                .totalReviews(200)
                .educations(consultantEducations.stream()
                        .map(consultantEducation -> new ConsultantEducationDTO(
                                consultantEducation.getInstitution(),
                                consultantEducation.getDegree(),
                                String.valueOf(consultantEducation.getYear())
                        ))
                        .collect(Collectors.toSet()))
                .languages(Set.of(consultantProfile.getLanguages()))
                .specializations(Collections.singleton(consultant.getSpecialization().getName()))
                .stats(new ConsultantStatDTO(
                        consultantStat.getPatientsHelped().length,
                        consultantStat.getSuccessRate(),
                        consultantStat.getAverageResponseTime(),
                        consultantStat.getSuccessRate()
                ))
                .availableSlots(
                       availableSlots
                )
                .reviews(consultationService.retrieveReviewsByConsultantId(consultantId))
                .build();

    }
    @Transactional(readOnly = true)
    public ConsultantProfileDetails getConsultantProfileDetails() {
        var userProjection = authUtils.getCurrentUser();
        var consultantProfileWithEducationProjectionOpt = consultantCustomRepository.findConsultantProjectionByUserId(userProjection.userId());
        if (consultantProfileWithEducationProjectionOpt.isPresent()) {
            var consultantProfile = consultantProfileWithEducationProjectionOpt.get();
            var consultantProfileDetails = ConsultantProfileFull.builder()
                    .address(consultantProfile.address())
                    .bio(consultantProfile.bio())
                    .dateOfBirth(consultantProfile.dateOfBirth().toString())
                    .email(userProjection.userPrincipal().getUsername())
                    .location(consultantProfile.location())
                    .experience(String.valueOf(consultantProfile.experience()))
                    .gender(userProjection.gender().name().toLowerCase())
                    .languages(consultantProfile.languages())
                    .specialty(consultantProfile.specialty().name())
                    .userName(consultantProfile.userName())
                    .phoneNumber(consultantProfile.phoneNumber())
                    .build();

            ConsultantEducationResponse educationResponse = consultantProfile.educations().stream()
                .map(consultantEducation -> new ConsultantEducationResponse(
                    String.valueOf(consultantEducation.year()),
                    consultantEducation.institution(),
                    consultantEducation.degree())).findAny().orElse(null);

            return new ConsultantProfileDetails(
                    consultantProfileDetails,
                    educationResponse
            );

        }
        throw new EntityNotFoundException("Could't find this customer's profile");
    }

    public ConsultantProfileDetails updateConsultantProfileDetails(ConsultantProfileUpdateRequest consultantProfileUpdateRequest) {
        String consultantId =  authUtils.getCurrentUser().userId();
        ConsultantEducation consultantEducation;
        Consultant consultant = consultantRepository.findByUserId(consultantId)
                .orElseThrow(() -> new EntityNotFoundException("No consultant of id %s exists".formatted(consultantId)));

        try {
            consultantEducation = consultantEducationRepository
                    .findAllByConsultant_UserId(consultantId)
                    .stream().findAny().orElse(ConsultantEducation.ofEmpty());
        } catch (NoSuchElementException ex) {
            consultantEducation = ConsultantEducation.ofEmpty();
        }
        ConsultantProfileFull profile = consultantProfileUpdateRequest.profile();
        if (Objects.nonNull(profile)) {
            if (Objects.nonNull(profile.specialty())) {
                    consultant.setSpecialization(medicalCategoryService.getMedicalCategoryByName(profile.specialty()));
            }

            if (Objects.nonNull(profile.address())) {
                Address addressToChange = getAddressToChange(consultant, profile);
                consultant.getProfile().setAddress(addressToChange);
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

            if (Objects.nonNull(profile.userName())) {
                var usernameToChange = consultant.getProfile().getUserName();
                if (Objects.nonNull(profile.userName().getFirstName())) {
                    usernameToChange.setFirstName(profile.userName().getFirstName());
                }
                if (Objects.nonNull(profile.userName().getLastName())) {
                    usernameToChange.setLastName(profile.userName().getLastName());
                }
                consultant.getProfile().setUserName(usernameToChange);
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

    private static Address getAddressToChange(Consultant consultant, ConsultantProfileFull profile) {
        Address addressToChange = consultant.getProfile().getAddress();
        if (Objects.isNull(addressToChange)) {
            addressToChange = new Address();
        }
        if (Objects.nonNull(profile.address().getState())){
            addressToChange.setState(profile.address().getState());
        }
        if (Objects.nonNull(profile.address().getCountry())){
            addressToChange.setCountry(profile.address().getCountry());
        }
        if (Objects.nonNull(profile.address().getCity())){
            addressToChange.setCity(profile.address().getCity());
        }
        return addressToChange;
    }

    public Consultant getReference(long id) {
        return consultantRepository.getReferenceById(id);
    }

}

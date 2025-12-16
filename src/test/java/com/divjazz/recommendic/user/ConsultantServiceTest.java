package com.divjazz.recommendic.user;

import com.divjazz.recommendic.global.exception.EntityNotFoundException;
import com.divjazz.recommendic.notification.app.service.AppNotificationService;
import com.divjazz.recommendic.security.service.SecurityService;
import com.divjazz.recommendic.user.controller.consultant.payload.ConsultantOnboardingRequest;
import com.divjazz.recommendic.user.controller.consultant.payload.ConsultantRegistrationParams;
import com.divjazz.recommendic.user.dto.CertificationDTO;
import com.divjazz.recommendic.user.enums.CertificateType;
import com.divjazz.recommendic.user.enums.Gender;
import com.divjazz.recommendic.user.enums.UserStage;
import com.divjazz.recommendic.user.model.Consultant;
import com.divjazz.recommendic.user.model.MedicalCategoryEntity;
import com.divjazz.recommendic.user.model.userAttributes.Address;
import com.divjazz.recommendic.user.model.userAttributes.ConsultantProfile;
import com.divjazz.recommendic.user.model.userAttributes.Role;
import com.divjazz.recommendic.user.model.userAttributes.UserName;
import com.divjazz.recommendic.user.model.userAttributes.credential.UserCredential;
import com.divjazz.recommendic.user.repository.ConsultantRepository;
import com.divjazz.recommendic.user.repository.certificationRepo.CertificationRepository;
import com.divjazz.recommendic.user.repository.certificationRepo.ConsultantEducationRepository;
import com.divjazz.recommendic.user.repository.confirmation.UserConfirmationRepository;
import com.divjazz.recommendic.user.service.ConsultantService;
import com.divjazz.recommendic.user.service.GeneralUserService;
import com.divjazz.recommendic.user.service.MedicalCategoryService;
import com.divjazz.recommendic.user.service.RoleService;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
public class ConsultantServiceTest {
    private static final Faker faker = new Faker();
    @Mock
    private  PasswordEncoder passwordEncoder;
    @Mock
    private  GeneralUserService userService;
    @Mock
    private  ConsultantRepository consultantRepository;
    @InjectMocks
    private ConsultantService consultantService;
    private Consultant consultant;
    @Mock
    private RoleService roleService;
    @Mock
    private MedicalCategoryService medicalCategoryService;
    @Mock
    private UserConfirmationRepository userConfirmationRepository;
    @Mock
    private ConsultantEducationRepository consultantEducationRepository;
    @Mock
    private CertificationRepository certificationRepository;
    @Mock
    private ApplicationEventPublisher applicationEventPublisher;
    @Mock
    private AppNotificationService appNotificationService;
    @Mock
    private SecurityService securityService;
    @BeforeEach
    void setup() {
        consultant = new Consultant(
                faker.internet().emailAddress(),
                Gender.MALE,
                new UserCredential(faker.text().text(20)), new Role(1L,"ROLE_CONSULTANT", "")
        );
        consultant.setSpecialization(new MedicalCategoryEntity(1,"Opthalmology","opthalmology", "some desc", "icon"));
        consultant.getUserPrincipal().setEnabled(true);
        consultant.setUserStage(UserStage.ACTIVE_USER);
        ConsultantProfile consultantProfile = ConsultantProfile.builder()
                .address(new Address(faker.address().city(), faker.address().state(), faker.address().country()))
                .dateOfBirth(faker.timeAndDate().birthday())
                .userName(new UserName(faker.name().firstName(), faker.name().lastName()))
                .locationOfInstitution(faker.location().work())
                .title(faker.job().title())
                .consultant(consultant)
                .build();
        consultant.setProfile(consultantProfile);
        consultant.setUserId("ConsultantId");
    }

    private static Stream<Arguments> getValidConsultantDTOParameters() {
        return Stream.of(
                Arguments.argumentSet("consultant requests",
                        new ConsultantRegistrationParams(
                                faker.name().firstName(),
                                faker.name().lastName(),
                                faker.internet().emailAddress(),
                                "kdsoidhosifbodinos",
                                faker.timeAndDate().birthday().toString(),
                                Gender.FEMALE.toString()
                        )
                )
        );
    }

    @ParameterizedTest
    @MethodSource("getValidConsultantDTOParameters")
    void givenValidParameterShouldCreateConsultantUser(ConsultantRegistrationParams consultantRegistrationParams) {
        var savedConsultant = new Consultant(
                consultantRegistrationParams.email(),
                Gender.valueOf(consultantRegistrationParams.gender().toUpperCase()),
                new UserCredential(consultantRegistrationParams.password()),
                new Role(1L,"ROLE_CONSULTANT", "")
        );
        var medicalCategoryEntity = new MedicalCategoryEntity(1,"Opthalmology","opthalmology", "some desc", "icon");
        savedConsultant.setSpecialization(medicalCategoryEntity);
        savedConsultant.getUserPrincipal().setEnabled(true);
        savedConsultant.setUserStage(UserStage.ACTIVE_USER);
        ConsultantProfile consultantProfile = ConsultantProfile.builder()
                .address(new Address(faker.address().city(), faker.address().state(), faker.address().country()))
                .dateOfBirth(faker.timeAndDate().birthday())
                .userName(new UserName(consultantRegistrationParams.firstName(), consultantRegistrationParams.lastName()))
                .locationOfInstitution(faker.location().work())
                .title(faker.job().title())
                .consultant(consultant)
                .build();
        savedConsultant.setProfile(consultantProfile);


        given(passwordEncoder.encode(anyString())).willReturn("Encoded Password String");
        given(userService.isUserExists(anyString())).willReturn(false);
        given(consultantRepository.save(any(Consultant.class))).willReturn(savedConsultant);
        given(roleService.getRoleByName(anyString())).willReturn(new Role("TEST","ROLE_TEST"));

        var result = consultantService.createConsultant(consultantRegistrationParams);

        assertThat(result.firstName()).isEqualTo(consultantRegistrationParams.firstName());
        assertThat(result.lastName()).isEqualTo(consultantRegistrationParams.lastName());
        assertThat(result.gender()).isEqualToIgnoringCase(consultantRegistrationParams.gender());
    }

    private static Stream<Arguments> getValidMedicalSpecialty() {
        return Stream.of(
                Arguments.argumentSet("Valid Medical Categories",
                        Set.of(new MedicalCategoryEntity(1,"Pediatrician", "pediatrician","Some desc","icon" )),
                        Set.of(new MedicalCategoryEntity(1,"Cardiology", "cardiology","Some desc","icon" )),
                        Set.of(new MedicalCategoryEntity(1,"Oncology", "oncology","Some desc","icon" ))
                )
        );
    }


    private final ConsultantOnboardingRequest onboardingRequest = ConsultantOnboardingRequest.builder()
            .specialization("oncology")
            .yearsOfExperience(6)
            .availableDays(Set.of("saturday"))
            .bio(faker.text().text(200))
            .certifications(faker.medicalProcedure().icd10())
            .consultationDuration(120)
            .consultationFee(20000)
            .currentWorkplace(faker.location().work())
            .medicalDegree(faker.university().degree())
            .resume(new CertificationDTO(faker.file().fileName(), faker.internet().url(), CertificateType.RESUME))
            .credentials(Set.of(new CertificationDTO(faker.file().fileName(), faker.internet().url(), CertificateType.RESUME)))
            .graduationYear(2019)
            .languages(Set.of("English", "French"))
            .licenseNumber(faker.medication().drugName())
            .preferredTimeSlots(Set.of("12am-1pm"))
            .profilePictureUrl(faker.avatar().image())
            .university(faker.university().name())
            .build();

    @Test
    void shouldSuccessfullyHandleUserOnboardingAndReturnTrue() {
        given(consultantRepository.findByUserId(anyString())).willReturn(Optional.of(consultant));
        given(medicalCategoryService.getMedicalCategoryById(anyString())).willReturn(new MedicalCategoryEntity(1,"Opthalmology","opthalmology", "some desc", "icon"));

        boolean result = consultantService.handleOnboarding(consultant.getUserId(), onboardingRequest);
        assertThat(result).isTrue();
    }
    @Test
    void shouldThrowIllegalArgumentExceptionIfInvalidMedicalCategories() {
        given(consultantRepository.findByUserId(anyString())).willReturn(Optional.of(consultant));
        given(medicalCategoryService.getMedicalCategoryById(anyString())).willThrow(new IllegalArgumentException());
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> consultantService.handleOnboarding(consultant.getUserId(), onboardingRequest));
    }
    @Test
    void shouldFailHandlingOnboardingAndReturnEntityNotFoundExceptionIfUserNotFound() {
        given(consultantRepository.findByUserId(anyString())).willReturn(Optional.empty());
        assertThatExceptionOfType(EntityNotFoundException.class).isThrownBy(() -> consultantService.handleOnboarding(consultant.getUserId(), onboardingRequest));
    }


    @Test
    void shouldGetConsultantByMedicalSpecialization() {
        given(consultantRepository.findBySpecialization(any(MedicalCategoryEntity.class))).willReturn(
               Set.of(consultant)
        );

        var result = consultantService.getConsultantsByCategory(new MedicalCategoryEntity(1,"Opthalmology","opthalmology", "some desc", "icon"));
        assertThat(result).hasSize(1);
        assertThat(result).isUnmodifiable();
    }
}

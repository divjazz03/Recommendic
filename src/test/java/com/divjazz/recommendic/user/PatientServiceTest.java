package com.divjazz.recommendic.user;

import com.divjazz.recommendic.global.exception.EntityNotFoundException;
import com.divjazz.recommendic.user.controller.patient.payload.PatientRegistrationParams;
import com.divjazz.recommendic.user.enums.Gender;
import com.divjazz.recommendic.user.enums.UserStage;
import com.divjazz.recommendic.user.model.MedicalCategoryEntity;
import com.divjazz.recommendic.user.model.Patient;
import com.divjazz.recommendic.user.model.userAttributes.Address;
import com.divjazz.recommendic.user.model.userAttributes.PatientProfile;
import com.divjazz.recommendic.user.model.userAttributes.Role;
import com.divjazz.recommendic.user.model.userAttributes.UserName;
import com.divjazz.recommendic.user.model.userAttributes.credential.UserCredential;
import com.divjazz.recommendic.user.repository.PatientRepository;
import com.divjazz.recommendic.user.repository.confirmation.UserConfirmationRepository;
import com.divjazz.recommendic.user.service.GeneralUserService;
import com.divjazz.recommendic.user.service.PatientService;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.BDDMockito.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class PatientServiceTest {
    private static final Faker faker = new Faker();
    @Mock
    private UserConfirmationRepository userConfirmationRepository;
    @Mock
    private GeneralUserService userService;
    @Mock
    private PasswordEncoder encoder;
    @Mock
    private ApplicationEventPublisher applicationEventPublisher;
    @Mock
    private PatientRepository patientRepository;
    @InjectMocks
    private PatientService patientService;

    private  Patient patient ;


    @BeforeEach
    void setup() {
        patient = new Patient(
                faker.internet().emailAddress(),
                Gender.MALE,
                new UserCredential(faker.text().text(20)),
                new Role(1L, "TEST_ROLE", "")
        );
        patient.getUserPrincipal().setEnabled(true);
        patient.addMedicalCategory(new MedicalCategoryEntity(1L, "cardiology", "desc"));
        patient.setUserStage(UserStage.ACTIVE_USER);

        PatientProfile patientProfile = PatientProfile.builder()
                .address(new Address(faker.address().city(), faker.address().state(), faker.address().country()))
                .phoneNumber(faker.phoneNumber().phoneNumber())
                .userName(new UserName(faker.name().firstName(), faker.name().lastName()))
                .patient(patient)
                .build();
    }

    static Stream<Arguments> getValidPatientDTOParameters() {
        return Stream.of(
                Arguments.of(
                        new PatientRegistrationParams(
                                faker.name().firstName(),
                                faker.name().lastName(),
                                faker.internet().emailAddress(),
                                faker.text().text(23),
                                faker.timeAndDate().birthday().toString(),
                                faker.gender().binaryTypes())
                )
        );
    }

    @ParameterizedTest
    @MethodSource("getValidPatientDTOParameters")
    void givenValidParameterShouldCreatePatientUser(PatientRegistrationParams registrationParams) {
        given(encoder.encode(anyString())).willReturn("Encoded Password String");
        given(userService.isUserExists(anyString())).willReturn(true);

        var result = patientService.createPatient(registrationParams);
        assertThat(result.firstName()).isEqualTo(registrationParams.firstName());

    }

    static Stream<Arguments> getValidMedicalCategories() {
        return Stream.of(
                Arguments.of(
                        List.of("pediatrician", "cardiology", "oncology")
                ),
                Arguments.of(
                        List.of("orthopedic surgery", "neurosurgery")
                )
        );
    }
    static Stream<Arguments> getInValidMedicalCategories() {
        return Stream.of(
                Arguments.of(
                        List.of("pediaician", "cardology", "onclogy")
                ),
                Arguments.of(
                        List.of("orthopedic ", "neusurgery")
                )
        );
    }

    @ParameterizedTest
    @MethodSource("getValidMedicalCategories")
    void shouldSuccessfullyHandleUserOnboardingAndReturnTrue(Set<String> medicalCategories) {
        given(patientRepository.findByUserId(anyString())).willReturn(Optional.of(patient));

        patientService.handleOnboarding(patient.getUserId(), medicalCategories);
    }
    @ParameterizedTest
    @MethodSource("getInValidMedicalCategories")
    void shouldThrowIllegalArgumentExceptionIfInvalidMedicalCategories(Set<String> invalidMedicalCategories) {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> patientService.handleOnboarding(patient.getUserId(), invalidMedicalCategories));
    }
    @ParameterizedTest
    @MethodSource("getValidMedicalCategories")
    void shouldFailHandlingOnboardingAndReturnEntityNotFoundExceptionIfUserNotFound(Set<String> medicalCategories) {
        given(patientRepository.findByUserId(anyString())).willReturn(Optional.empty());
        assertThatExceptionOfType(EntityNotFoundException.class).isThrownBy(() -> patientService.handleOnboarding(patient.getUserId(), medicalCategories));
    }

}

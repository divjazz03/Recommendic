package com.divjazz.recommendic.user;

import com.divjazz.recommendic.global.exception.EntityNotFoundException;
import com.divjazz.recommendic.user.dto.PatientDTO;
import com.divjazz.recommendic.user.enums.Gender;
import com.divjazz.recommendic.user.enums.UserStage;
import com.divjazz.recommendic.user.model.Patient;
import com.divjazz.recommendic.user.model.userAttributes.Address;
import com.divjazz.recommendic.user.model.userAttributes.PatientProfile;
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
                new UserCredential(faker.text().text(20))
        );
        patient.getUserPrincipal().setEnabled(true);
        patient.setMedicalCategories(new String[]{});
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
                        new PatientDTO(
                                new UserName("test_user1_firstname", "test_user1_firstname"),
                                "test_user1@test.com",
                                "+23490393848",
                                Gender.MALE,
                                new Address("test_user1_city", "test_user1_state", "test_user1_country"),
                                "test_user1_password"
                        )
                )
        );
    }

    @ParameterizedTest
    @MethodSource("getValidPatientDTOParameters")
    void givenValidParameterShouldCreatePatientUser(PatientDTO patientDTO) {
        given(encoder.encode(anyString())).willReturn("Encoded Password String");
        given(userService.isUserExists(anyString())).willReturn(true);

        var result = patientService.createPatient(patientDTO);

        assertThat(result.address()).isEqualTo(patientDTO.address());
        assertThat(result.firstName()).isEqualTo(patientDTO.userName().getFirstName());

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
    void shouldSuccessfullyHandleUserOnboardingAndReturnTrue(List<String> medicalCategories) {
        given(patientRepository.findByUserId(anyString())).willReturn(Optional.of(patient));

        patientService.handleOnboarding(patient.getUserId(), medicalCategories);
    }
    @ParameterizedTest
    @MethodSource("getInValidMedicalCategories")
    void shouldThrowIllegalArgumentExceptionIfInvalidMedicalCategories(List<String> invalidMedicalCategories) {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> patientService.handleOnboarding(patient.getUserId(), invalidMedicalCategories));
    }
    @ParameterizedTest
    @MethodSource("getValidMedicalCategories")
    void shouldFailHandlingOnboardingAndReturnEntityNotFoundExceptionIfUserNotFound(List<String> medicalCategories) {
        given(patientRepository.findByUserId(anyString())).willReturn(Optional.empty());
        assertThatExceptionOfType(EntityNotFoundException.class).isThrownBy(() -> patientService.handleOnboarding(patient.getUserId(), medicalCategories));
    }

}

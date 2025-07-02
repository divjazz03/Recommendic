package com.divjazz.recommendic.unit.user;

import com.divjazz.recommendic.global.exception.EntityNotFoundException;
import com.divjazz.recommendic.user.dto.PatientDTO;
import com.divjazz.recommendic.user.enums.Gender;
import com.divjazz.recommendic.user.event.UserEvent;
import com.divjazz.recommendic.user.model.Patient;
import com.divjazz.recommendic.user.model.UserConfirmation;
import com.divjazz.recommendic.user.model.userAttributes.Address;
import com.divjazz.recommendic.user.model.userAttributes.UserName;
import com.divjazz.recommendic.user.model.userAttributes.credential.UserCredential;
import com.divjazz.recommendic.user.repository.PatientRepository;
import com.divjazz.recommendic.user.repository.confirmation.UserConfirmationRepository;
import com.divjazz.recommendic.user.service.GeneralUserService;
import com.divjazz.recommendic.user.service.PatientService;
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
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
public class PatientServiceTest {
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

    private final Patient patient  = new Patient(
            new UserName("test_user1_firstname", "test_user1_firstname"),
            "test_user1@test.com",
            "+234905593953",
            Gender.FEMALE,
            new Address("test_user1_city", "test_user1_state", "test_user1_country"),
            new UserCredential("test_user1_password")
    );

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
        given(userService.isUserNotExists(anyString())).willReturn(true);

        var result = patientService.createPatient(patientDTO);

        assertThat(result.address()).isEqualTo(patientDTO.address());
        assertThat(result.firstName()).isEqualTo(patientDTO.userName().getFirstName());

        then(patientRepository).should(times(1)).save(any(Patient.class));
        then(userConfirmationRepository).should(times(1)).save(any(UserConfirmation.class));
        then(applicationEventPublisher).should(times(1)).publishEvent(any(UserEvent.class));


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

        boolean result = patientService.handleOnboarding(patient.getUserId(), medicalCategories);
        assertThat(result).isTrue();
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

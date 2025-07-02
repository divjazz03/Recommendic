package com.divjazz.recommendic.unit.user;

import com.divjazz.recommendic.global.exception.EntityNotFoundException;
import com.divjazz.recommendic.user.dto.ConsultantDTO;
import com.divjazz.recommendic.user.dto.PatientDTO;
import com.divjazz.recommendic.user.enums.Gender;
import com.divjazz.recommendic.user.enums.MedicalCategoryEnum;
import com.divjazz.recommendic.user.event.UserEvent;
import com.divjazz.recommendic.user.model.Consultant;
import com.divjazz.recommendic.user.model.UserConfirmation;
import com.divjazz.recommendic.user.model.userAttributes.Address;
import com.divjazz.recommendic.user.model.userAttributes.UserName;
import com.divjazz.recommendic.user.model.userAttributes.credential.UserCredential;
import com.divjazz.recommendic.user.repository.ConsultantRepository;
import com.divjazz.recommendic.user.repository.confirmation.UserConfirmationRepository;
import com.divjazz.recommendic.user.service.ConsultantService;
import com.divjazz.recommendic.user.service.GeneralUserService;
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

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class ConsultantServiceTest {

    @Mock
    private  UserConfirmationRepository userConfirmationRepository;
    @Mock
    private  PasswordEncoder passwordEncoder;
    @Mock
    private  GeneralUserService userService;
    @Mock
    private  ApplicationEventPublisher applicationEventPublisher;
    @Mock
    private  ConsultantRepository consultantRepository;
    @InjectMocks
    private ConsultantService consultantService;

    private Consultant consultant;
    @BeforeEach
    void setup() {
        consultant = new Consultant(
                new UserName("test_user2_firstname", "test_user2_firstname"),
                "test_user2@test.com",
                "+234905593953",
                Gender.MALE,
                new Address("test_user2_city", "test_user2_state", "test_user2_country"),
                new UserCredential("test_user2_password")
        );
        consultant.setMedicalCategory(MedicalCategoryEnum.PEDIATRICIAN);
    }

    static Stream<Arguments> getValidConsultantDTOParameters() {
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
    @MethodSource("getValidConsultantDTOParameters")
    void givenValidParameterShouldCreateConsultantUser(ConsultantDTO consultantDTO) {
        given(passwordEncoder.encode(anyString())).willReturn("Encoded Password String");
        given(userService.isUserNotExists(anyString())).willReturn(true);

        var result = consultantService.createConsultant(consultantDTO);

        assertThat(result.address()).isEqualTo(consultantDTO.address());
        assertThat(result.firstName()).isEqualTo(consultantDTO.userName().getFirstName());

        then(consultantRepository).should(times(1)).save(any(Consultant.class));
        then(userConfirmationRepository).should(times(1)).save(any(UserConfirmation.class));
        then(applicationEventPublisher).should(times(1)).publishEvent(any(UserEvent.class));
    }

    static Stream<Arguments> getValidMedicalSpecialty() {
        return Stream.of(
                Arguments.of(
                        "pediatrician"
                ),
                Arguments.of(
                        "orthopedic surgery"
                )
        );
    }
    static Stream<Arguments> getInValidMedicalSpecialty() {
        return Stream.of(
                Arguments.of(
                        "cardology"
                ),
                Arguments.of(
                       "neusurgery"
                )
        );
    }

    @ParameterizedTest
    @MethodSource("getValidMedicalSpecialty")
    void shouldSuccessfullyHandleUserOnboardingAndReturnTrue(String medicalSpecialization) {
        given(consultantRepository.findByUserId(anyString())).willReturn(Optional.of(consultant));

        boolean result = consultantService.handleOnboarding(consultant.getUserId(), medicalSpecialization);
        assertThat(result).isTrue();
    }
    @ParameterizedTest
    @MethodSource("getInValidMedicalSpecialty")
    void shouldThrowIllegalArgumentExceptionIfInvalidMedicalCategories(String invalidMedicalSpecialty) {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> consultantService.handleOnboarding(consultant.getUserId(), invalidMedicalSpecialty));
    }
    @ParameterizedTest
    @MethodSource("getValidMedicalSpecialty")
    void shouldFailHandlingOnboardingAndReturnEntityNotFoundExceptionIfUserNotFound(String medicalCategories) {
        given(consultantRepository.findByUserId(anyString())).willReturn(Optional.empty());

        assertThatExceptionOfType(EntityNotFoundException.class).isThrownBy(() -> consultantService.handleOnboarding(consultant.getUserId(), medicalCategories));
    }


    @Test
    void shouldGetConsultantByMedicalSpecialization() {
        given(consultantRepository.findByMedicalCategoryIgnoreCase(anyString())).willReturn(
                List.of(consultant)
        );

        var result = consultantService.getConsultantsByCategory(MedicalCategoryEnum.PEDIATRICIAN);
        assertThat(result).hasSize(1);
        assertThat(result).isUnmodifiable();
    }
}

package com.divjazz.recommendic.user;

import com.divjazz.recommendic.global.exception.EntityNotFoundException;
import com.divjazz.recommendic.security.UserPrincipal;
import com.divjazz.recommendic.user.enums.Gender;
import com.divjazz.recommendic.user.enums.LoginType;
import com.divjazz.recommendic.user.enums.UserType;
import com.divjazz.recommendic.user.model.Consultant;
import com.divjazz.recommendic.user.model.Patient;
import com.divjazz.recommendic.user.model.User;
import com.divjazz.recommendic.user.model.userAttributes.Role;
import com.divjazz.recommendic.user.model.userAttributes.credential.UserCredential;
import com.divjazz.recommendic.user.repository.AdminRepository;
import com.divjazz.recommendic.user.repository.ConsultantRepository;
import com.divjazz.recommendic.user.repository.PatientRepository;
import com.divjazz.recommendic.user.repository.UserRepository;
import com.divjazz.recommendic.user.service.GeneralUserService;
import com.divjazz.recommendic.user.service.UserLoginRetryHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.datafaker.Faker;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    private static final Faker faker = new Faker();
    @Mock
    private UserLoginRetryHandler userLoginRetryHandler;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private PatientRepository patientRepository;
    @Mock
    private ConsultantRepository consultantRepository;
    @Mock
    private AdminRepository adminRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private GeneralUserService generalUserService;

    @Test
    void givenValidEmailShouldReturnConsultant() {
        var userToReturn = new Consultant(
                faker.internet().emailAddress(),
                Gender.MALE,
                new UserCredential(faker.text().text(20))
        );
        given(patientRepository.findByUserPrincipal_Email(anyString())).willReturn(Optional.empty());
        given(adminRepository.findByUserPrincipal_Email(anyString())).willReturn(Optional.empty());
        given(consultantRepository.findByUserPrincipal_Email(anyString())).willReturn(Optional.of(userToReturn));

        var actualReturnedUser = generalUserService.retrieveUserByEmail("test_user@test.com");

        assertThat(actualReturnedUser.getUserPrincipal().getUsername())
                .isEqualTo(userToReturn.getUserPrincipal().getUsername());
    }
    @Test
    void givenValidEmailShouldReturnPatient() {
        var userToReturn = new Patient(
                faker.internet().emailAddress(),
                Gender.MALE,
                new UserCredential(faker.text().text(20))
        );
        given(patientRepository.findByUserPrincipal_Email(anyString())).willReturn(Optional.of(userToReturn));
        given(adminRepository.findByUserPrincipal_Email(anyString())).willReturn(Optional.empty());
        given(consultantRepository.findByUserPrincipal_Email(anyString())).willReturn(Optional.empty());

        var actualReturnedUser = generalUserService.retrieveUserByEmail("test_user@test.com");

        assertThat(actualReturnedUser.getUserPrincipal().getUsername())
                .isEqualTo(userToReturn.getUserPrincipal().getUsername());

    }
    @Test
    void givenInvalidEmailShouldThrowNotFound() {
        given(patientRepository.findByUserPrincipal_Email(anyString())).willReturn(Optional.empty());
        given(adminRepository.findByUserPrincipal_Email(anyString())).willReturn(Optional.empty());
        given(consultantRepository.findByUserPrincipal_Email(anyString())).willReturn(Optional.empty());

        assertThatExceptionOfType(EntityNotFoundException.class).isThrownBy(() -> generalUserService.retrieveUserByEmail("test_user@test.com"));

    }

    @Test
    void givenValidUserIdShouldReturnConsultant() {
        var userId = UUID.randomUUID().toString();
        var userToReturn = new Consultant(
                faker.internet().emailAddress(),
                Gender.MALE,
                new UserCredential(faker.text().text(20))
        );
        given(patientRepository.findByUserId(anyString())).willReturn(Optional.empty());
        given(adminRepository.findByUserId(anyString())).willReturn(Optional.empty());
        given(consultantRepository.findByUserId(anyString())).willReturn(Optional.of(userToReturn));

        var actualReturnedUser = generalUserService.retrieveUserByUserId(userId);

        assertThat(actualReturnedUser.getUserPrincipal().getUsername())
                .isEqualTo(userToReturn.getUserPrincipal().getUsername());
    }
    @Test
    void givenValidUserIdShouldReturnPatient() {
        var userId = UUID.randomUUID().toString();
        var userToReturn = new Patient(
                faker.internet().emailAddress(),
                Gender.MALE,
                new UserCredential(faker.text().text(20))
        );

        given(patientRepository.findByUserId(anyString())).willReturn(Optional.of(userToReturn));
        given(adminRepository.findByUserId(anyString())).willReturn(Optional.empty());
        given(consultantRepository.findByUserId(anyString())).willReturn(Optional.empty());

        var actualReturnedUser = generalUserService.retrieveUserByUserId(userId);

        assertThat(actualReturnedUser.getUserPrincipal().getUsername()).isEqualTo(userToReturn.getUserPrincipal().getUsername());
    }

    @Test
    void shouldCallHandleFailedAttemptWhenLoginFailed() {
        var user = User.builder()
                .userPrincipal(UserPrincipal.builder()
                        .email("test_user@test.com")
                        .role(Role.PATIENT)
                        .enabled(true)
                        .userCredential(new UserCredential("password"))
                        .build())
                .userId(UUID.randomUUID().toString())
                .userType(UserType.CONSULTANT)
                .build();
        generalUserService.updateLoginAttempt(user, LoginType.LOGIN_FAILED);

        then(userLoginRetryHandler).should(times(1)).handleFailedAttempts(eq(user.getUserPrincipal().getUsername()));
        then(userLoginRetryHandler).should(never()).handleSuccessFulAttempt(anyString());
        then(consultantRepository).shouldHaveNoInteractions();
        then(patientRepository).shouldHaveNoInteractions();
        then(adminRepository).shouldHaveNoInteractions();
    }

    @Test
    void shouldCallHandleSuccessAttemptForPatientWhenLoginSuccess() {
        var user = new Patient(
                faker.internet().emailAddress(),
                Gender.MALE,
                new UserCredential(faker.text().text(20))
        );
        generalUserService.updateLoginAttempt(user, LoginType.LOGIN_SUCCESS);

        then(userLoginRetryHandler).should(never()).handleFailedAttempts(anyString());
        then(userLoginRetryHandler).should(times(1)).handleSuccessFulAttempt(anyString());
        then(consultantRepository).should(never()).save(any());
        then(patientRepository).should(times(1)).save(any());
        then(adminRepository).should(never()).save(any());


    }
    @Test
    void shouldCallHandleSuccessAttemptForConsultantWhenLoginSuccess() {
        var user = new Consultant(
                faker.internet().emailAddress(),
                Gender.MALE,
                new UserCredential(faker.text().text(20))
        );
        generalUserService.updateLoginAttempt(user, LoginType.LOGIN_SUCCESS);

        then(userLoginRetryHandler).should(never()).handleFailedAttempts(anyString());
        then(userLoginRetryHandler).should(times(1)).handleSuccessFulAttempt(anyString());
        then(consultantRepository).should(times(1)).save(any());
        then(patientRepository).should(never()).save(any());
        then(adminRepository).should(never()).save(any());


    }

    @Test
    void shouldEnablePatientUserWithValidUserId() {
        var userToReturn = new Patient(
                faker.internet().emailAddress(),
                Gender.MALE,
                new UserCredential(faker.text().text(20))
        );

        given(patientRepository.findByUserId(anyString())).willReturn(Optional.of(userToReturn));
        given(adminRepository.findByUserId(anyString())).willReturn(Optional.empty());
        given(consultantRepository.findByUserId(anyString())).willReturn(Optional.empty());

        generalUserService.enableUser(userToReturn.getUserId());
        assertThat(userToReturn.getUserPrincipal().isEnabled()).isTrue();
    }

    @Test
    void shouldEnableConsultantUserWithValidUserId() {
        var userToReturn = new Consultant(
                faker.internet().emailAddress(),
                Gender.MALE,
                new UserCredential(faker.text().text(20))
        );

        given(patientRepository.findByUserId(anyString())).willReturn(Optional.empty());
        given(adminRepository.findByUserId(anyString())).willReturn(Optional.empty());
        given(consultantRepository.findByUserId(anyString())).willReturn(Optional.of(userToReturn));

        generalUserService.enableUser(userToReturn.getUserId());
        assertThat(userToReturn.getUserPrincipal().isEnabled()).isTrue();
    }


}

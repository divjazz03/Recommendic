package com.divjazz.recommendic.user;

import com.divjazz.recommendic.global.exception.EntityNotFoundException;
import com.divjazz.recommendic.security.UserPrincipal;
import com.divjazz.recommendic.user.dto.UserDTO;
import com.divjazz.recommendic.user.enums.Gender;
import com.divjazz.recommendic.user.enums.LoginType;
import com.divjazz.recommendic.user.enums.UserStage;
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
import com.divjazz.recommendic.user.repository.projection.RoleProjection;
import com.divjazz.recommendic.user.repository.projection.UserPrincipalProjection;
import com.divjazz.recommendic.user.repository.projection.UserProjection;
import com.divjazz.recommendic.user.service.GeneralUserService;
import com.divjazz.recommendic.user.service.UserLoginRetryHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.datafaker.Faker;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
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
        given(consultantRepository.findByEmailReturningProjection(anyString())).willReturn(userProjection);

        var actualReturnedUser = generalUserService.retrieveUserByEmail("test_user@test.com");

        assertThat(actualReturnedUser.getUserPrincipal().getUsername())
                .isEqualTo(userProjection.getUserPrincipal().getUsername());
    }
    @Test
    void givenValidEmailShouldReturnPatient() {
        given(patientRepository.findByEmailReturningProjection(anyString())).willReturn(userProjection);

        var actualReturnedUser = generalUserService.retrieveUserByEmail("test_user@test.com");

        assertThat(actualReturnedUser.getUserPrincipal().getUsername())
                .isEqualTo(userProjection.getUserPrincipal().getUsername());

    }
    @Test
    void givenInvalidEmailShouldThrowNotFound() {
        given(patientRepository.findByEmailReturningProjection(anyString())).willReturn(null);
        given(adminRepository.findByEmailReturningProjection(anyString())).willReturn(null);
        given(consultantRepository.findByEmailReturningProjection(anyString())).willReturn(null);

        assertThatExceptionOfType(EntityNotFoundException.class).isThrownBy(() -> generalUserService.retrieveUserByEmail("test_user@test.com"));

    }

    @Test
    void givenValidUserIdShouldReturnConsultant() {
        var userId = UUID.randomUUID().toString();
        var userToReturn = new Consultant(
                faker.internet().emailAddress(),
                Gender.MALE,
                new UserCredential(faker.text().text(20)),
                new Role(1L,"ROLE_TEST", "")
        );
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
                new UserCredential(faker.text().text(20)),
                new Role(1L,"ROLE_TEST", "")
        );

        given(patientRepository.findByUserId(anyString())).willReturn(Optional.of(userToReturn));

        var actualReturnedUser = generalUserService.retrieveUserByUserId(userId);

        assertThat(actualReturnedUser.getUserPrincipal().getUsername()).isEqualTo(userToReturn.getUserPrincipal().getUsername());
    }

    @Test
    void shouldCallHandleFailedAttemptWhenLoginFailed() {

        generalUserService.updateLoginAttempt(userProjection, LoginType.LOGIN_FAILED);

        then(userLoginRetryHandler).should(times(1)).handleFailedAttempts(eq(userProjection.getUserPrincipal().getUsername()));
        then(userLoginRetryHandler).should(never()).handleSuccessFulAttempt(anyString());
        then(consultantRepository).shouldHaveNoInteractions();
        then(patientRepository).shouldHaveNoInteractions();
        then(adminRepository).shouldHaveNoInteractions();
    }


    @Test
    void shouldEnablePatientUserWithValidUserId() {
        var userToReturn = new Patient(
                faker.internet().emailAddress(),
                Gender.MALE,
                new UserCredential(faker.text().text(20)),
                new Role(1L,"ROLE_TEST", "")
        );
        userToReturn.setUserId("Dskdnmlsdkncls");
        given(patientRepository.findByUserId(anyString())).willReturn(Optional.of(userToReturn));

        generalUserService.enableUser(userToReturn.getUserId());
        assertThat(userToReturn.getUserPrincipal().isEnabled()).isTrue();
    }

    @Test
    void shouldEnableConsultantUserWithValidUserId() {
        var userToReturn = new Consultant(
                faker.internet().emailAddress(),
                Gender.MALE,
                new UserCredential(faker.text().text(20)),
                new Role(1L, "ROLE_TEST","")
        );
        userToReturn.setUserId("djsdnsldksldk");

        given(consultantRepository.findByUserId(anyString())).willReturn(Optional.of(userToReturn));

        generalUserService.enableUser(userToReturn.getUserId());
        assertThat(userToReturn.getUserPrincipal().isEnabled()).isTrue();
    }

    UserProjection userProjection = new UserProjection(){

        @Override
        public Long getId() {
            return 0L;
        }

        @Override
        public String getUserId() {
            return "";
        }

        @Override
        public Gender getGender() {
            return null;
        }

        @Override
        public LocalDateTime getLastLogin() {
            return null;
        }

        @Override
        public UserType getUserType() {
            return UserType.PATIENT;
        }

        @Override
        public UserStage getUserStage() {
            return UserStage.ACTIVE_USER;
        }

        @Override
        public UserPrincipalProjection getUserPrincipal() {
            return new UserPrincipalProjection() {
                @Override
                public UserPrincipal toUserPrincipal() {
                    return UserPrincipalProjection.super.toUserPrincipal();
                }

                @Override
                public boolean isEnabled() {
                    return false;
                }

                @Override
                public String getUsername() {
                    return "email";
                }

                @Override
                public String getEmail() {
                    return "email";
                }

                @Override
                public RoleProjection getRole() {
                    return new RoleProjection() {
                        @Override
                        public String getName() {
                            return "TEST";
                        }

                        @Override
                        public String getPermissions() {
                            return "ROLE_TEST";
                        }
                    };
                }

                @Override
                public UserCredential getUserCredential() {
                    return new UserCredential("djfbiksbvksuub");
                }
            };
        }
    };


}

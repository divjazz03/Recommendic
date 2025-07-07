package com.divjazz.recommendic.security;

import com.divjazz.recommendic.global.exception.EntityNotFoundException;
import com.divjazz.recommendic.security.service.AuthService;
import com.divjazz.recommendic.user.dto.LoginRequest;
import com.divjazz.recommendic.user.enums.Gender;
import com.divjazz.recommendic.user.enums.LoginType;
import com.divjazz.recommendic.user.enums.UserStage;
import com.divjazz.recommendic.user.enums.UserType;
import com.divjazz.recommendic.user.exception.ConfirmationTokenExpiredException;
import com.divjazz.recommendic.user.model.User;
import com.divjazz.recommendic.user.model.UserConfirmation;
import com.divjazz.recommendic.user.model.userAttributes.Address;
import com.divjazz.recommendic.user.model.userAttributes.Role;
import com.divjazz.recommendic.user.model.userAttributes.UserName;
import com.divjazz.recommendic.user.repository.confirmation.UserConfirmationRepository;
import com.divjazz.recommendic.user.service.GeneralUserService;
import com.divjazz.recommendic.user.service.UserLoginRetryHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @Mock
    private GeneralUserService generalUserService;
    @Mock
    private UserLoginRetryHandler userLoginRetryHandler;
    @Mock
    private CustomAuthenticationProvider customAuthenticationProvider;
    @Mock
    private UserConfirmationRepository userConfirmationRepository;
    @InjectMocks
    private AuthService authService;
    @Mock
    private MockHttpServletRequest httpServletRequest;

    static Stream<Arguments> getValidLoginRequest() {
        return Stream.of(
                Arguments.of(
                        new LoginRequest("testuser1@test.com", "testuser1password")
                ),
                Arguments.of(
                        new LoginRequest("testuser2@test.com", "testuser2password")
                )
        );
    }
    @ParameterizedTest
    @MethodSource("getValidLoginRequest")
    void givenRequestIfAccountLockedShouldThrowLockedException(LoginRequest loginRequest) {
        given(userLoginRetryHandler.isAccountLocked(loginRequest.email())).willReturn(true);

        assertThatExceptionOfType(LockedException.class).isThrownBy(() -> authService.handleUserLogin(loginRequest,httpServletRequest));
    }
    @ParameterizedTest
    @MethodSource("getValidLoginRequest")
    void givenIncorrectPasswordShouldThrowBadCredentialsException(LoginRequest loginRequest) {
        var user = User.builder()
                .email(loginRequest.email())
                .phoneNumber("")
                .userStage(UserStage.ONBOARDING)
                .userType(UserType.CONSULTANT)
                .gender(Gender.FEMALE)
                .role(Role.CONSULTANT)
                .enabled(false)
                        .build();
        given(userLoginRetryHandler.isAccountLocked(anyString())).willReturn(false);
        given(generalUserService.retrieveUserByEmail(anyString())).willReturn(user);
        given(customAuthenticationProvider.authenticate(any(Authentication.class))).willThrow(new BadCredentialsException("Invalid Credentials"));

        assertThatExceptionOfType(BadCredentialsException.class).isThrownBy(() -> authService.handleUserLogin(loginRequest,httpServletRequest));
        then(generalUserService).should(times(1)).updateLoginAttempt(any(User.class),eq(LoginType.LOGIN_FAILED));
    }

    @ParameterizedTest
    @MethodSource("getValidLoginRequest")
    void givenCorrectPasswordAndEmailShouldReturnALoginResponse(LoginRequest loginRequest) {
        var user = User.builder()
                .email(loginRequest.email())
                .phoneNumber("")
                .userStage(UserStage.ONBOARDING)
                .userType(UserType.CONSULTANT)
                .gender(Gender.FEMALE)
                .role(Role.CONSULTANT)
                .enabled(false)
                .userName(new UserName("test_first_name", "test_last_name"))
                .address(new Address("test_city", "test_state", "test_country"))
                .build();
        given(userLoginRetryHandler.isAccountLocked(anyString())).willReturn(false);
        given(httpServletRequest.getSession()).willReturn(new MockHttpSession());
        given(customAuthenticationProvider.authenticate(any(Authentication.class)))
                .willReturn(ApiAuthentication
                        .authenticated(user, "[protected]",
                                List.of(new SimpleGrantedAuthority(user.getRole().getPermissions()))));
        var result = authService.handleUserLogin(loginRequest, httpServletRequest);
        assertThat(result.address()).isEqualTo(user.getAddress());
        assertThat(result.firstName()).isEqualTo(user.getUserNameObject().getFirstName());
        assertThat(result.lastName()).isEqualTo(user.getUserNameObject().getLastName());
        assertThat(result.role()).isEqualTo(user.getRole().getName());
        assertThat(result.userStage()).isEqualTo(user.getUserStage().toString());

    }
    @Test
    void givenInvalidTokenShouldThrowNotFoundException() {
        given(userConfirmationRepository.findByKey(anyString())).willReturn(Optional.empty());

        assertThatExceptionOfType(EntityNotFoundException.class).isThrownBy(() -> authService.handleConfirmationTokenValidation(UUID.randomUUID().toString()));

        then(generalUserService).shouldHaveNoInteractions();
    }

    @Test
    void givenExistsTokenButExpiredShouldThrowTokenExpiredException() {
        var confirmationToken = new UserConfirmation();
        confirmationToken.setKey(UUID.randomUUID().toString());
        confirmationToken.setExpiry(LocalDateTime.now());
        given(userConfirmationRepository.findByKey(anyString())).willReturn(Optional.of(confirmationToken));

        assertThatExceptionOfType(ConfirmationTokenExpiredException.class)
                .isThrownBy(() -> authService.handleConfirmationTokenValidation(confirmationToken.getKey()));
    }
    @Test
    void givenExistsTokenAndNotExpiredShouldReturnConfirmed() {
        var confirmationToken = new UserConfirmation();
        confirmationToken.setKey(UUID.randomUUID().toString());
        confirmationToken.setExpiry(LocalDateTime.now().plusDays(1));
        confirmationToken.setUserId(UUID.randomUUID().toString());
        given(userConfirmationRepository.findByKey(anyString())).willReturn(Optional.of(confirmationToken));

        var result = authService.handleConfirmationTokenValidation(confirmationToken.getKey());
        assertThat(result).isEqualTo("confirmed");
    }
}

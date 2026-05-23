package com.divjazz.recommendic.security.service;

import com.divjazz.recommendic.global.exception.EntityNotFoundException;
import com.divjazz.recommendic.security.exception.AuthenticationException;
import com.divjazz.recommendic.security.model.AuthToken;
import com.divjazz.recommendic.security.repository.AuthTokenRepository;
import com.divjazz.recommendic.user.dto.LoginRequest;
import com.divjazz.recommendic.user.dto.LoginResponse;
import com.divjazz.recommendic.user.enums.LoginType;
import com.divjazz.recommendic.user.exception.ConfirmationTokenExpiredException;
import com.divjazz.recommendic.user.model.User;
import com.divjazz.recommendic.user.model.UserConfirmation;
import com.divjazz.recommendic.user.repository.confirmation.UserConfirmationRepository;
import com.divjazz.recommendic.user.service.GeneralUserService;
import com.divjazz.recommendic.user.service.UserLoginRetryHandler;
import com.github.f4b6a3.ulid.UlidCreator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final GeneralUserService generalUserService;

    private final UserLoginRetryHandler userLoginRetryHandler;
    private final AuthenticationManager authenticationManager;
    private final UserConfirmationRepository userConfirmationRepository;
    private final JWTService jwtService;
    private final AuthTokenRepository authTokenRepository;

    public LoginResponse handleUserLogin(LoginRequest loginRequest) {

        if (userLoginRetryHandler.isAccountLocked(loginRequest.email())) {
            throw new LockedException("Your account has been locked due to too many tries. Please try again later");
        }
        UsernamePasswordAuthenticationToken unAuthenticated = UsernamePasswordAuthenticationToken
                .unauthenticated(loginRequest.email(), loginRequest.password());
        try {
            Authentication authenticated = authenticationManager.authenticate(unAuthenticated);
            var authenticatedUser = generalUserService.retrieveUserByEmail((String) unAuthenticated.getPrincipal());
            generalUserService.updateLoginAttempt(authenticatedUser, LoginType.LOGIN_SUCCESS);
            var refreshToken = generateRefreshToken(authenticatedUser.getUserId(),
                    authenticatedUser.getUserPrincipal().getRole().getName());
            var accessToken = generateAccessToken(authenticatedUser.getUserPrincipal());
            return new LoginResponse(authenticatedUser.getUserId(),
                    authenticatedUser.getUserPrincipal().getRole().getName(),
                    refreshToken,
                    accessToken,
                    authenticatedUser.getUserStage().toString());
        } catch (BadCredentialsException ex) {
            var unauthenticatedUser = generalUserService.retrieveUserByEmail((String) unAuthenticated.getPrincipal());
            generalUserService.updateLoginAttempt(unauthenticatedUser, LoginType.LOGIN_FAILED);
            throw new AuthenticationException("Email and password combination invalid try again");
        }
    }

    private String generateRefreshToken(String userId, String role) {
        var _authToken = AuthToken.builder()
                .expiresAt(Instant.now().plus(1, ChronoUnit.DAYS))
                .userId(userId)
                .token(UlidCreator.getMonotonicUlid().toString())
                .userType(role)
                .build();
        var authToken = authTokenRepository.save(_authToken);
        return authToken.getToken();
    }
    private String generateAccessToken(UserDetails userDetails) {
        return jwtService.generateToken(userDetails);
    }

    @Transactional
    public String handleConfirmationTokenValidation(String token) {
        UserConfirmation userConfirmation = userConfirmationRepository.findByKey(token)
                .orElseThrow(() -> new EntityNotFoundException("Confirmation with token: %s does not exist".formatted(token)));
        if (userConfirmation.isExpired()) {
            throw new ConfirmationTokenExpiredException();
        }
        String userId = userConfirmation.getUserId();
        generalUserService.enableUser(userId);

        return "confirmed";
    }

    public String handleTokenRefresh(String refreshToken) {
        AuthToken authToken = authTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new EntityNotFoundException("Token not found"));
        boolean isExpired = Instant.now().isAfter(authToken.getExpiresAt());
        if (isExpired) {
            throw new AuthenticationException("Token expired please login");
        }
        User user = generalUserService.retrieveUserByUserId(authToken.getUserId());
        return jwtService.generateToken(user.getUserPrincipal());
    }

    public UserDetails getUserFromJwt(Jwt jwt) {
        var subject = jwt.getSubject();

        var user = generalUserService.retrieveUserByEmail(subject);

        return user.getUserPrincipal();
    }
}

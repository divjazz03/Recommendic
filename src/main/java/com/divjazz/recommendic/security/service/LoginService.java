package com.divjazz.recommendic.security.service;

import com.divjazz.recommendic.security.ApiAuthentication;
import com.divjazz.recommendic.security.CustomAuthenticationProvider;
import com.divjazz.recommendic.security.exception.AuthenticationException;
import com.divjazz.recommendic.user.dto.LoginRequest;
import com.divjazz.recommendic.user.dto.LoginResponse;
import com.divjazz.recommendic.user.enums.LoginType;
import com.divjazz.recommendic.user.exception.ConfirmationTokenExpiredException;
import com.divjazz.recommendic.user.model.User;
import com.divjazz.recommendic.user.model.UserConfirmation;
import com.divjazz.recommendic.user.repository.confirmation.UserConfirmationRepository;
import com.divjazz.recommendic.user.service.GeneralUserService;
import com.divjazz.recommendic.user.service.UserLoginRetryHandler;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final GeneralUserService generalUserService;

    private final UserLoginRetryHandler userLoginRetryHandler;
    private final CustomAuthenticationProvider customAuthenticationProvider;
    private final UserConfirmationRepository userConfirmationRepository;

    public LoginResponse handleUserLogin(LoginRequest loginRequest, HttpServletRequest httpServletRequest) {

        if (userLoginRetryHandler.isAccountLocked(loginRequest.getEmail())) {
            throw new LockedException("Your account has been locked due to too many tries. Please try again later");
        }
        ApiAuthentication unAuthenticated = ApiAuthentication
                .unAuthenticated(loginRequest.getEmail(), loginRequest.getPassword());
        try {
            UsernamePasswordAuthenticationToken authenticated = (UsernamePasswordAuthenticationToken) customAuthenticationProvider.authenticate(unAuthenticated);

            var authenticatedUser = (User) authenticated.getPrincipal();
            generalUserService.updateLoginAttempt(authenticatedUser, LoginType.LOGIN_SUCCESS);
            httpServletRequest.getSession().setAttribute("user", authenticatedUser.getEmail());
            httpServletRequest.getSession().setAttribute("role", authenticatedUser.getRole().getName());
            return new LoginResponse(authenticatedUser.getUserId(),
                    authenticatedUser.getUserNameObject().getFirstName(),
                    authenticatedUser.getUserNameObject().getLastName(),
                    authenticatedUser.getRole().getName(),
                    authenticatedUser.getAddress(),
                    authenticatedUser.getUserStage());
        } catch (BadCredentialsException ex) {
            var unauthenticatedUser = generalUserService.retrieveUserByEmail(unAuthenticated.getEmail());
            generalUserService.updateLoginAttempt(unauthenticatedUser, LoginType.LOGIN_FAILED);
            throw new AuthenticationException("Invalid credentials try again");
        }
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
}

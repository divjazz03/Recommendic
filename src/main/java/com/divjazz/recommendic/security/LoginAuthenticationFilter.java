package com.divjazz.recommendic.security;

import com.divjazz.recommendic.Response;
import com.divjazz.recommendic.security.jwt.service.JwtService;
import com.divjazz.recommendic.user.dto.LoginRequest;
import com.divjazz.recommendic.user.dto.LoginResponse;
import com.divjazz.recommendic.user.enums.LoginType;
import com.divjazz.recommendic.user.exception.UserNotFoundException;
import com.divjazz.recommendic.user.model.User;
import com.divjazz.recommendic.user.service.GeneralUserService;
import com.divjazz.recommendic.user.service.UserLoginRetryHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.IOException;
import java.util.Map;

import static com.divjazz.recommendic.security.constant.Constants.LOGIN_PATH;
import static com.divjazz.recommendic.utils.RequestUtils.getResponse;
import static com.divjazz.recommendic.utils.RequestUtils.handleErrorResponse;


public class LoginAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private final GeneralUserService userService;
    private final JwtService jwtService;
    private final ObjectMapper objectMapper;
    private final UserLoginRetryHandler userLoginRetryHandler;
    Logger log = LoggerFactory.getLogger(LoginAuthenticationFilter.class);

    public LoginAuthenticationFilter(AuthenticationManager authenticationManager,
                                     JwtService jwtService,
                                     GeneralUserService userService,
                                     ObjectMapper objectMapper, UserLoginRetryHandler userLoginRetryHandler) {
        super(new AntPathRequestMatcher(LOGIN_PATH, HttpMethod.POST.name()), authenticationManager);
        this.userService = userService;
        this.jwtService = jwtService;
        this.objectMapper = objectMapper;
        this.userLoginRetryHandler = userLoginRetryHandler;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            var loginRequest = objectMapper.
                    readValue(request.getInputStream(), LoginRequest.class);
            if (userLoginRetryHandler.isAccountLocked(loginRequest.getEmail())) {
                throw new LockedException("Your account has been locked due to too many tries. Please try again later");
            }
            var authentication = ApiAuthentication.unAuthenticated(loginRequest.getEmail(), loginRequest.getPassword());
            var authenticationResult = getAuthenticationManager().authenticate(authentication);
            if (!authenticationResult.isAuthenticated()) {
                userService.updateLoginAttempt((User) authenticationResult.getPrincipal(), LoginType.LOGIN_FAILED);
            }
            return authenticationResult;
        } catch (IOException | UserNotFoundException | AuthenticationException e) {
            log.error(e.getMessage());
            handleErrorResponse(request, response, e);
            return null;
        }
    }


    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException {
        var auth = (ApiAuthentication) authResult;
        var user = (User) auth.getPrincipal();
        userService.updateLoginAttempt(user, LoginType.LOGIN_SUCCESS);
        var httpResponse = sendResponse(request, response, user);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.OK.value());
        try (var out = response.getOutputStream()) {
            var mapper = new ObjectMapper();
            mapper.writeValue(out, httpResponse);
        }
    }

    private Response sendResponse(HttpServletRequest request, HttpServletResponse response, User user) {
        jwtService.addCookie(response, user, TokenType.ACCESS);
        var userResponse = new LoginResponse(user.getUserId(),
                user.getUserNameObject().getFirstName(),
                user.getUserNameObject().getLastName(),
                user.getRole().toString(),
                user.getAddress(),
                user.getUserStage());
        return getResponse(request, Map.of("user", userResponse), "login Success", HttpStatus.OK);
    }
}






























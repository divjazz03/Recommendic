package com.divjazz.recommendic.security;

import com.divjazz.recommendic.security.jwt.service.JwtService;
import com.divjazz.recommendic.Response;
import com.divjazz.recommendic.user.dto.LoginRequest;
import com.divjazz.recommendic.user.dto.UserDTO;
import com.divjazz.recommendic.user.enums.LoginType;
import com.divjazz.recommendic.user.model.User;
import com.divjazz.recommendic.user.service.GeneralUserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.IOException;
import java.util.Map;

import static com.divjazz.recommendic.security.constant.Constants.LOGIN_PATH;
import static com.divjazz.recommendic.utils.RequestUtils.getResponse;
import static com.divjazz.recommendic.utils.RequestUtils.handleErrorResponse;
import static com.fasterxml.jackson.core.JsonParser.Feature.AUTO_CLOSE_SOURCE;


public class LoginAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    Logger log = LoggerFactory.getLogger(LoginAuthenticationFilter.class);
    private final GeneralUserService userService;
    private final JwtService jwtService;
    public LoginAuthenticationFilter(AuthenticationManager authenticationManager,
                                     JwtService jwtService,
                                     GeneralUserService userService) {
        super(new AntPathRequestMatcher(LOGIN_PATH, HttpMethod.POST.name()), authenticationManager);
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        try {
            var loginRequest = new ObjectMapper()
                    .configure(AUTO_CLOSE_SOURCE, true)
                    .readValue(request.getInputStream(), LoginRequest.class);
            userService.updateLoginAttempt(loginRequest.getEmail(), LoginType.LOGIN_ATTEMPT);
            var authentication = ApiAuthentication.unAuthenticated(loginRequest.getEmail(), loginRequest.getPassword());
            return getAuthenticationManager().authenticate(authentication);
        } catch (Exception e) {
            log.error(e.getMessage());
            handleErrorResponse(request, response, e);
            return null;
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        var user = (User) authResult.getPrincipal();
        userService.updateLoginAttempt(user.getEmail(), LoginType.LOGIN_SUCCESS);
        var httpResponse = sendResponse(request,response,user);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.OK.value());
        try (var out = response.getOutputStream()){
            var mapper = new ObjectMapper();
            mapper.writeValue(out, httpResponse);
        }
    }

    private Response sendResponse(HttpServletRequest request, HttpServletResponse response, User user) {
        jwtService.addCookie(response,user,TokenType.ACCESS);
        var userDTO = new UserDTO(
                user.getCreatedBy(),
                user.getUpdatedBy(),
                user.getUserId(),
                user.getUserNameObject().getFirstName(),
                user.getUserNameObject().getLastName(),
                user.getProfilePicture().getPictureUrl(),
                user.getProfilePicture().getName(),
                user.getLastLogin().toString(),
                user.getCreatedAt().toString(),
                user.getUpdatedAt().toString(),
                user.getRole().getName(),
                user.isAccountNonExpired(),
                user.isEnabled()
        );
        return getResponse(request, Map.of("user", userDTO), "login Success", HttpStatus.OK);
    }
}






























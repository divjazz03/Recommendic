package com.divjazz.recommendic.security;

import com.divjazz.recommendic.exception.EntityNotFoundException;
import com.divjazz.recommendic.security.domain.TokenData;
import com.divjazz.recommendic.security.exception.InvalidTokenException;
import com.divjazz.recommendic.security.exception.TokenNotFoundException;
import com.divjazz.recommendic.security.jwt.service.JwtService;

import static com.divjazz.recommendic.security.TokenType.*;
import static com.divjazz.recommendic.RequestUtils.getErrorResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;


import javax.security.sasl.AuthenticationException;
import java.io.IOException;
import java.util.Optional;
import java.util.regex.Pattern;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    public static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private final JwtService jwtService;
    private final CustomUserDetailsService userService;
    public static final Pattern ACTUATOR_PATHS = Pattern.compile("^/actuator(?:/[\\w.-]+)*$");
    public static final Pattern AUTH_PATHS = Pattern.compile("^/api/v1/auth/(login|logout|email-token)$");
    public static final Pattern FAVICON = Pattern.compile("^/favicon.ico");
    public static final Pattern USER_PATH = Pattern.compile("^/api/v1/(patient|consultant|admin)/create$");
    public static final Pattern MEDICAL_PATH = Pattern.compile("^/api/v1/medical_categories/$");
    public static final Pattern DRUG_API_PATH = Pattern.compile("^/api/v1/search/.*$");
    public static final Pattern LOGIN_PATH= Pattern.compile("^/user/login");
    public static final Pattern SWAGGER_PATH = Pattern.compile("^/swagger-ui/.*|/api-docs(?:/[\\w.-]+)*$");
    public JwtAuthenticationFilter(JwtService jwtService, CustomUserDetailsService userService) {
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request,
                                    @NotNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain) throws ServletException, IOException {

        var requestURI = request.getRequestURI();

        var shouldNotBeIgnored = !(FAVICON.matcher(requestURI).find() ||
                ACTUATOR_PATHS.matcher(requestURI).find() ||
                USER_PATH.matcher(requestURI).find() ||
                DRUG_API_PATH.matcher(requestURI).find() ||
                LOGIN_PATH.matcher(requestURI).find() ||
                AUTH_PATHS.matcher(requestURI).find() ||
                SWAGGER_PATH.matcher(requestURI).find() ||
                MEDICAL_PATH.matcher(requestURI).find());

        try {

            if (shouldNotBeIgnored) {

                final Optional<String> authorizationAccessToken = jwtService.extractToken(request, ACCESS.getValue());
                final String authorizationHeader = request.getHeader("Authorization");
                String userId = null;
                String jwtToken = null;

                if (authorizationAccessToken.isPresent()) {
                    jwtToken = authorizationAccessToken.get();
                    userId = jwtService.getClaimsValue(jwtToken, Claims::getSubject);
                } else if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                    jwtToken = authorizationHeader.substring(7);
                    userId = jwtService.getClaimsValue(jwtToken, Claims::getSubject);
                } else throw new TokenNotFoundException();

                if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = userService.loadUserByUserId(userId);

                    if (jwtService.validateToken(request,userDetails) || jwtService.validateTokenInHeader(request,userDetails)) {
                        var userPermissions = jwtService.getTokenData(jwtToken, userDetails, TokenData::getGrantedAuthorities);
                        logger.debug("granted authority for current user is {}", userPermissions);
                        var authentication = ApiAuthentication.authenticated(userDetails,userDetails.getPassword(),userDetails.getAuthorities());
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        logger.info("authenticated user with userId :{}", userId);
                        filterChain.doFilter(request,response);
                    } else {
                        throw new InvalidTokenException();
                    }
                } else {
                    throw new AuthenticationException("No user found in authentication context");
                }
            } else {
                filterChain.doFilter(request,response);
            }
        } catch (EntityNotFoundException | TokenNotFoundException | InvalidTokenException e) {
            var errorResponse = getErrorResponse(
                    HttpStatus.EXPECTATION_FAILED,
                    e
            );
            try (var out = response.getOutputStream()){
                var mapper = new ObjectMapper();
                mapper.writeValue(out, errorResponse);
            }
        }
    }
}

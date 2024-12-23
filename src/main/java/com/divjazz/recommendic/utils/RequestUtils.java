package com.divjazz.recommendic.utils;

import com.divjazz.recommendic.Response;
import com.divjazz.recommendic.user.exception.CertificateNotFoundException;
import com.divjazz.recommendic.user.exception.NoSuchMedicalCategory;
import com.divjazz.recommendic.user.exception.UserAlreadyExistsException;
import com.divjazz.recommendic.user.exception.UserNotFoundException;
import com.divjazz.recommendic.user.model.User;
import com.divjazz.recommendic.user.service.GeneralUserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import java.nio.file.AccessDeniedException;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import static java.time.LocalDateTime.now;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.exception.ExceptionUtils.getRootCauseMessage;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

public class RequestUtils {

    private static final BiConsumer<HttpServletResponse, Response> writeResponse = (httpServletResponse, response) -> {
                try {
                    var outputStream = httpServletResponse.getOutputStream();
                    new ObjectMapper().writeValue(outputStream, response);
                    outputStream.flush();
                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage());
                }
            };

    private static final BiFunction<Exception, HttpStatus, String> errorReason = (exception, httpStatus) -> {
        if (httpStatus.isSameCodeAs(HttpStatus.FORBIDDEN))
            return "You do not have enough permission";
        if (httpStatus.isSameCodeAs(HttpStatus.UNAUTHORIZED))
            return "You are not logged in";
        if (exception instanceof DisabledException ||
                exception instanceof LockedException ||
                exception instanceof BadCredentialsException ||
                exception instanceof CredentialsExpiredException ||
                exception instanceof UserNotFoundException ||
                exception instanceof CertificateNotFoundException ||
                exception instanceof NoSuchMedicalCategory ||
                exception instanceof UserAlreadyExistsException)
            return exception.getMessage();
        if (httpStatus.is5xxServerError())
            return "An Internal server error occured";
        else
            return "An error occurred. Please try again ";
    };
    public static Response getResponse(HttpServletRequest httpServletRequest,
                                       Map<?,?> data,
                                       String message,
                                       HttpStatus status){
        return new Response(
                now().format(ISO_LOCAL_DATE_TIME),
                status.value(),
                httpServletRequest.getRequestURI(),
                HttpStatus.valueOf(status.value()),
                message,
                EMPTY,
                (Objects.isNull(data))? Collections.EMPTY_MAP : data
        );
    }
    public static Response getErrorResponse(HttpServletRequest httpServletRequest,
                                            HttpStatus status,
                                            Exception exception){
        return new Response(
                now().format(ISO_LOCAL_DATE_TIME),
                status.value(),
                httpServletRequest.getRequestURI(),
                HttpStatus.valueOf(status.value()),
                errorReason.apply(exception,status),
                getRootCauseMessage(exception),
                Collections.emptyMap()
        );
    }
    public static Response getErrorResponse(HttpServletRequest httpServletRequest,
                                            HttpServletResponse httpServletResponse,
                                            Exception exception, HttpStatus status){
       httpServletResponse.setContentType(APPLICATION_JSON_VALUE);
       httpServletResponse.setStatus(status.value());
       return new Response(
               now().format(ISO_LOCAL_DATE_TIME),
               status.value(),
               httpServletRequest.getRequestURI(),
               HttpStatus.valueOf(status.value()),
               errorReason.apply(exception,status),
               getRootCauseMessage(exception),
               Collections.emptyMap()
       );
    }

    public static void handleErrorResponse(HttpServletRequest request, HttpServletResponse response,Exception e) {
        if (e instanceof AccessDeniedException ) {
            Response apiREsponse = getErrorResponse(request,response,e,HttpStatus.FORBIDDEN);
            writeResponse.accept(response,apiREsponse);
        } else if (e instanceof UserNotFoundException) {
            Response apiResponse = getErrorResponse(request,response,e,HttpStatus.NOT_FOUND);
            writeResponse.accept(response,apiResponse);
        } else {
            Response apiResponse = getErrorResponse(request,response,e,HttpStatus.EXPECTATION_FAILED);
            writeResponse.accept(response,apiResponse);
        }
    }

    public static User getCurrentUser(Authentication authentication, GeneralUserService userService) {
        return userService.retrieveUserByEmail(((UserDetails) authentication.getPrincipal()).getUsername());
    }
}

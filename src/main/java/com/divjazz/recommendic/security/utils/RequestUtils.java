package com.divjazz.recommendic.security.utils;

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
import org.springframework.http.HttpStatusCode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
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

    private static final BiConsumer<HttpServletResponse, Response<?>> writeResponse = (httpServletResponse,  response) -> {
        try {
            var outputStream = httpServletResponse.getOutputStream();
            new ObjectMapper().writeValue(outputStream, response);
            outputStream.flush();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    };

    private static final BiFunction<Exception, HttpStatusCode, String> errorReason = (exception, httpStatus) -> {
        if (httpStatus.isSameCodeAs(HttpStatus.FORBIDDEN))
            return "You do not have enough permission";
        if (exception instanceof AuthenticationException ||
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

    public static<T> Response<T> getResponse(
                                       T data,
                                       String message,
                                       HttpStatusCode status) {
        return new Response<T>(
                now().format(ISO_LOCAL_DATE_TIME),
                status.value(),
                HttpStatus.valueOf(status.value()),
                message,
                EMPTY,
                data
        );
    }

    public static Response<String> getErrorResponse(
                                            HttpStatusCode status,
                                            Exception exception) {
        return new Response<>(
                now().format(ISO_LOCAL_DATE_TIME),
                status.value(),
                HttpStatus.valueOf(status.value()),
                errorReason.apply(exception, status),
                getRootCauseMessage(exception),
                exception.getMessage()
        );
    }

    public static void handleErrorResponse(HttpServletRequest request, HttpServletResponse response, Exception e) {
        switch (e) {
            case AccessDeniedException _ -> {
                writeResponse.accept(response, getErrorResponse(HttpStatus.FORBIDDEN, e));
            }
            case UserNotFoundException _ -> {
                writeResponse.accept(response, getErrorResponse(HttpStatus.NOT_FOUND, e));
            }
            case AuthenticationException _ ->
                    writeResponse.accept(response, getErrorResponse(HttpStatus.UNAUTHORIZED, e));
            case null, default -> {
                writeResponse.accept(response, getErrorResponse(HttpStatus.EXPECTATION_FAILED, e));
            }
        }
    }
}

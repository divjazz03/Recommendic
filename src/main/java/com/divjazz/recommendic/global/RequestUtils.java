package com.divjazz.recommendic.global;

import com.divjazz.recommendic.global.exception.EntityNotFoundException;
import com.divjazz.recommendic.user.exception.NoSuchMedicalCategory;
import com.divjazz.recommendic.user.exception.UserAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.core.AuthenticationException;

import java.util.function.BiFunction;

import static java.time.LocalDateTime.now;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.exception.ExceptionUtils.getRootCauseMessage;

public class RequestUtils {

    private static final BiFunction<Exception, HttpStatusCode, String> errorReason = (exception, httpStatus) -> {
        if (httpStatus.isSameCodeAs(HttpStatus.FORBIDDEN))
            return "You do not have enough permission";
        if (httpStatus.is5xxServerError())
            return "An Internal server error occurred";
        else
            return exception.getMessage();
    };

    public static<T> Response<T> getResponse(
                                       T data,
                                       HttpStatusCode status) {
        return new Response<>(
                now().format(ISO_LOCAL_DATE_TIME),
                status.value(),
                HttpStatus.valueOf(status.value()),
                "Success",
                EMPTY,
                data
        );
    }

    public static <T> Response<T> getErrorResponse(
                                            HttpStatusCode status,
                                            Exception exception, T data) {
        return new Response<>(
                now().format(ISO_LOCAL_DATE_TIME),
                status.value(),
                HttpStatus.valueOf(status.value()),
                errorReason.apply(exception, status),
                exception.getClass().getCanonicalName(),
                data
        );
    }
    public static <T> Response<T> getErrorResponse(
            HttpStatusCode status,
            Exception exception) {
        return new Response<>(
                now().format(ISO_LOCAL_DATE_TIME),
                status.value(),
                HttpStatus.valueOf(status.value()),
                errorReason.apply(exception, status),
                exception.getClass().getCanonicalName(),
                null
        );
    }
}

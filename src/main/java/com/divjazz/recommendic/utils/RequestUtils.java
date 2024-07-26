package com.divjazz.recommendic.utils;

import com.divjazz.recommendic.user.domain.Response;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Map;

public class RequestUtils {
    public static Response getResponse(HttpServletRequest httpServletRequest,
                                       Map<?,?> data,
                                       String message,
                                       HttpStatus status){
        return new Response(
                LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                status.value(),
                httpServletRequest.getRequestURI(),
                HttpStatus.valueOf(status.value()),
                message,
                EMPTY,
                data
        );
    }
    public static Response getErrorResponse(HttpServletRequest httpServletRequest,
                                            HttpStatus status,
                                            Exception exception){
        return new Response(
                LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                status.value(),
                httpServletRequest.getRequestURI(),
                HttpStatus.valueOf(status.value()),
                exception.getMessage(),
                exception.getClass().getName(),
                Collections.emptyMap()
        );
    }
}

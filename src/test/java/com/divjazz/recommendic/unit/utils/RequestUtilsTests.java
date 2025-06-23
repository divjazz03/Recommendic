package com.divjazz.recommendic.unit.utils;

import com.divjazz.recommendic.Response;
import com.divjazz.recommendic.exception.EntityNotFoundException;
import com.divjazz.recommendic.user.exception.NoSuchMedicalCategory;
import com.divjazz.recommendic.user.exception.UserAlreadyExistsException;
import com.divjazz.recommendic.RequestUtils;

import static org.junit.jupiter.api.Assertions.*;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import org.apache.commons.lang3.exception.ExceptionUtils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
public class RequestUtilsTests {
    private static final String TEST_MESSAGE = "test message";

    @ParameterizedTest
    @MethodSource
    public void getErrorResponseShouldReturnAResponseObjectWithTheAppropriateException(Exception e){
        var response = RequestUtils.getErrorResponse(HttpStatus.EXPECTATION_FAILED, e);
        var expectedResponse = new Response<>(
                LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                HttpStatus.EXPECTATION_FAILED.value(),
                HttpStatus.EXPECTATION_FAILED,
                e.getMessage(),
                ExceptionUtils.getRootCauseMessage(e),
                e.getMessage());
        assertEquals(expectedResponse.exception(), response.exception());
    }
    private static Stream<Arguments> getErrorResponseShouldReturnAResponseObjectWithTheAppropriateException(){
        return Stream.of(
                Arguments.of(new UserAlreadyExistsException("e")),
                Arguments.of(new EntityNotFoundException("")),
                Arguments.of(new NoSuchMedicalCategory())
        );
    }

    @ParameterizedTest
    @MethodSource
    public void getErrorResponseShouldReturnAResponseObjectWithTheAppropriateStatusCodeAndMessage(HttpStatus status, Exception e){
        var response = RequestUtils.getErrorResponse(status, e);
        var expectedResponse = new Response<>(
                LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                status.value(),
                status,
                e.getMessage(),
                ExceptionUtils.getRootCauseMessage(e),
                e.getMessage());

        assertEquals(expectedResponse.status(), response.status());
        assertEquals(expectedResponse.message(), response.message());
    }

    private static Stream<Arguments> getErrorResponseShouldReturnAResponseObjectWithTheAppropriateStatusCodeAndMessage(){
        return Stream.of(
                Arguments.of(HttpStatus.EXPECTATION_FAILED, new UserAlreadyExistsException("e")),
                Arguments.of(HttpStatus.NOT_FOUND, new EntityNotFoundException("")),
                Arguments.of(HttpStatus.NOT_FOUND, new NoSuchMedicalCategory())
        );
    }

    @ParameterizedTest
    @MethodSource
    public <T> void getResponseShouldReturnAResponseObjectWithCorrectDataIfItExistsAndResponseCode(T data, HttpStatus status){

        var response = RequestUtils.getResponse(data,TEST_MESSAGE,status);
        var expectedResponse = new Response<>(
                LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                status.value(),
                status,
                TEST_MESSAGE,
                EMPTY,
                data
        );
        assertEquals(expectedResponse.exception(),response.exception());
        assertEquals(expectedResponse.message(), response.message());
        assertEquals(expectedResponse.data(), response.data());
        assertEquals(expectedResponse.status(),response.status());
    }
    private static Stream<Arguments> getResponseShouldReturnAResponseObjectWithCorrectDataIfItExistsAndResponseCode(){
        return Stream.of(
                Arguments.of(Map.of("consultant", "consultant"), HttpStatus.OK),
                Arguments.of(Collections.EMPTY_MAP, HttpStatus.OK)
        );
    }

    @Test
    public void getResponseShouldReturnNullIfDataIsNull(){
        var response = RequestUtils.getResponse(null,TEST_MESSAGE,HttpStatus.OK);
        assertNull(response.data());
    }

}

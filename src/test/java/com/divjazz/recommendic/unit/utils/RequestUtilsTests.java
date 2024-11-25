package com.divjazz.recommendic.unit.utils;

import com.divjazz.recommendic.Response;
import com.divjazz.recommendic.user.exception.CertificateNotFoundException;
import com.divjazz.recommendic.user.exception.NoSuchMedicalCategory;
import com.divjazz.recommendic.user.exception.UserAlreadyExistsException;
import com.divjazz.recommendic.user.exception.UserNotFoundException;
import com.divjazz.recommendic.utils.RequestUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import org.apache.commons.lang3.exception.ExceptionUtils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Stream;

//@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class RequestUtilsTests {

    private HttpServletRequest mockHttpServletRequest;
    private static final String TEST_MESSAGE = "test message";

    @BeforeEach
    public void setup() {
        mockHttpServletRequest = Mockito.mock(HttpServletRequest.class);
    }
    @ParameterizedTest
    @MethodSource
    public void getErrorResponseShouldReturnAResponseObjectWithTheAppropriateException(Exception e){
        var response = RequestUtils.getErrorResponse(mockHttpServletRequest, HttpStatus.EXPECTATION_FAILED, e);
        var expectedResponse = new Response(
                LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                HttpStatus.EXPECTATION_FAILED.value(),
                mockHttpServletRequest.getRequestURI(),
                HttpStatus.EXPECTATION_FAILED,
                e.getMessage(),
                ExceptionUtils.getRootCauseMessage(e),
                Collections.emptyMap());
        assertEquals(expectedResponse.exception(), response.exception());
    }
    private static Stream<Arguments> getErrorResponseShouldReturnAResponseObjectWithTheAppropriateException(){
        return Stream.of(
                Arguments.of(new UserAlreadyExistsException("e")),
                Arguments.of(new UserNotFoundException()),
                Arguments.of(new CertificateNotFoundException(" ? ")),
                Arguments.of(new NoSuchMedicalCategory())
        );
    }

    @ParameterizedTest
    @MethodSource
    public void getErrorResponseShouldReturnAResponseObjectWithTheAppropriateStatusCodeAndMessage(HttpStatus status, Exception e){
        var response = RequestUtils.getErrorResponse(mockHttpServletRequest, status, e);
        var expectedResponse = new Response(
                LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                status.value(),
                mockHttpServletRequest.getRequestURI(),
                status,
                e.getMessage(),
                ExceptionUtils.getRootCauseMessage(e),
                Collections.emptyMap());

        assertEquals(expectedResponse.status(), response.status());
        assertEquals(expectedResponse.message(), response.message());
    }

    private static Stream<Arguments> getErrorResponseShouldReturnAResponseObjectWithTheAppropriateStatusCodeAndMessage(){
        return Stream.of(
                Arguments.of(HttpStatus.EXPECTATION_FAILED, new UserAlreadyExistsException("e")),
                Arguments.of(HttpStatus.NOT_FOUND, new UserNotFoundException()),
                Arguments.of(HttpStatus.NOT_FOUND, new CertificateNotFoundException(" ? ")),
                Arguments.of(HttpStatus.NOT_FOUND, new NoSuchMedicalCategory())
        );
    }

    @ParameterizedTest
    @MethodSource
    public void getResponseShouldReturnAResponseObjectWithCorrectDataIfItExistsAndResponseCode(Map<?,?> data, HttpStatus status){

        var response = RequestUtils.getResponse(mockHttpServletRequest,data,TEST_MESSAGE,status);
        var expectedResponse = new Response(
                LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                status.value(),
                mockHttpServletRequest.getRequestURI(),
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
    public void getResponseShouldReturnEmptyMapIfDataIsNull(){
        var response = RequestUtils.getResponse(mockHttpServletRequest,null,TEST_MESSAGE,HttpStatus.OK);
        assertEquals(Collections.EMPTY_MAP, response.data());
    }

}

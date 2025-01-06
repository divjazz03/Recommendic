package com.divjazz.recommendic;

import com.divjazz.recommendic.security.exception.AuthenticationException;
import com.divjazz.recommendic.security.exception.InvalidTokenException;
import com.divjazz.recommendic.security.exception.TokenNotFoundException;
import com.divjazz.recommendic.user.exception.CertificateNotFoundException;
import com.divjazz.recommendic.user.exception.NoSuchMedicalCategory;
import com.divjazz.recommendic.user.exception.UserAlreadyExistsException;
import com.divjazz.recommendic.user.exception.UserNotFoundException;
import com.divjazz.recommendic.utils.RequestUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalControllerExceptionAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(NoSuchMedicalCategory.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Response> handleInvalidMedicalCategory(RuntimeException ex, HttpServletRequest request) {

        return new ResponseEntity<>(RequestUtils.getErrorResponse(request, HttpStatus.BAD_REQUEST, ex), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Response> handleUserNotFound(RuntimeException ex, HttpServletRequest request) {

        return new ResponseEntity<>(RequestUtils.getErrorResponse(request, HttpStatus.NOT_FOUND, ex), HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(UserAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<Response> handleUserAlreadyExists(RuntimeException ex, HttpServletRequest request) {

        return new ResponseEntity<>(RequestUtils.getErrorResponse(request, HttpStatus.CONFLICT, ex), HttpStatus.CONFLICT);
    }
    @ExceptionHandler(CertificateNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Response> handleCertificationNotFound(RuntimeException ex, HttpServletRequest request) {
        return new ResponseEntity<>(RequestUtils.getErrorResponse(request, HttpStatus.NOT_FOUND, ex), HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<Response> handleAuthentication(RuntimeException ex, HttpServletRequest request) {
        return new ResponseEntity<>(RequestUtils.getErrorResponse(request, HttpStatus.UNAUTHORIZED, ex), HttpStatus.UNAUTHORIZED);
    }
    @ExceptionHandler(InvalidTokenException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<Response> handleInvalidToken(RuntimeException ex, HttpServletRequest request) {
        return new ResponseEntity<>(RequestUtils.getErrorResponse(request, HttpStatus.UNAUTHORIZED, ex), HttpStatus.UNAUTHORIZED);
    }
    @ExceptionHandler(TokenNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Response> handleTokenNotFound(RuntimeException ex, HttpServletRequest request) {
        return new ResponseEntity<>(RequestUtils.getErrorResponse(request, HttpStatus.NOT_FOUND, ex), HttpStatus.NOT_FOUND);
    }


}

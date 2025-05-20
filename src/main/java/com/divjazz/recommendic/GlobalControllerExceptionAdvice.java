package com.divjazz.recommendic;

import com.divjazz.recommendic.security.exception.InvalidTokenException;
import com.divjazz.recommendic.security.exception.LoginFailedException;
import com.divjazz.recommendic.security.exception.TokenNotFoundException;
import com.divjazz.recommendic.user.exception.CertificateNotFoundException;
import com.divjazz.recommendic.user.exception.NoSuchMedicalCategory;
import com.divjazz.recommendic.user.exception.UserAlreadyExistsException;
import com.divjazz.recommendic.user.exception.UserNotFoundException;
import com.divjazz.recommendic.security.utils.RequestUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

@RestControllerAdvice
public class GlobalControllerExceptionAdvice {

    @ExceptionHandler(NoSuchMedicalCategory.class)
    public ResponseEntity<Response<String>> handleInvalidMedicalCategory(NoSuchMedicalCategory ex) {

        return new ResponseEntity<>(RequestUtils.getErrorResponse(HttpStatus.BAD_REQUEST, ex), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Response<String>> handleUserNotFound(UserNotFoundException ex) {

        return new ResponseEntity<>(RequestUtils.getErrorResponse(HttpStatus.NOT_FOUND, ex), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<Response<String>> handleUserAlreadyExists(UserAlreadyExistsException ex) {

        return new ResponseEntity<>(RequestUtils.getErrorResponse(HttpStatus.CONFLICT, ex), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(CertificateNotFoundException.class)
    public ResponseEntity<Response<String>> handleCertificationNotFound(CertificateNotFoundException ex) {
        return new ResponseEntity<>(RequestUtils.getErrorResponse(HttpStatus.NOT_FOUND, ex), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<Response<String>> handleInvalidToken(InvalidTokenException ex) {
        return new ResponseEntity<>(RequestUtils.getErrorResponse(HttpStatus.UNAUTHORIZED, ex), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(TokenNotFoundException.class)
    public ResponseEntity<Response<String>> handleTokenNotFound(TokenNotFoundException ex) {
        return new ResponseEntity<>(RequestUtils.getErrorResponse(HttpStatus.NOT_FOUND, ex), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(LoginFailedException.class)
    public ResponseEntity<Response<String>> handleLoginFailedException(LoginFailedException ex) {
        return new ResponseEntity<>(RequestUtils.getErrorResponse(HttpStatus.UNAUTHORIZED, ex), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<Response<String>> handleHttpClientError(HttpClientErrorException ex) {
        return new ResponseEntity<>(RequestUtils.getResponse(ex.getResponseBodyAsString(), "failed", ex.getStatusCode()), ex.getStatusCode());
    }


}

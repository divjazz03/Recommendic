package com.divjazz.recommendic;

import com.divjazz.recommendic.security.exception.InvalidTokenException;
import com.divjazz.recommendic.security.exception.LoginFailedException;
import com.divjazz.recommendic.security.exception.TokenNotFoundException;
import com.divjazz.recommendic.user.exception.CertificateNotFoundException;
import com.divjazz.recommendic.user.exception.NoSuchMedicalCategory;
import com.divjazz.recommendic.user.exception.UserAlreadyExistsException;
import com.divjazz.recommendic.user.exception.UserNotFoundException;
import com.divjazz.recommendic.utils.RequestUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalControllerExceptionAdvice {

    @ExceptionHandler(NoSuchMedicalCategory.class)
    public ResponseEntity<Response> handleInvalidMedicalCategory(NoSuchMedicalCategory ex, HttpServletRequest request) {

        return new ResponseEntity<>(RequestUtils.getErrorResponse(request, HttpStatus.BAD_REQUEST, ex), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Response> handleUserNotFound(UserNotFoundException ex, HttpServletRequest request) {

        return new ResponseEntity<>(RequestUtils.getErrorResponse(request, HttpStatus.NOT_FOUND, ex), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<Response> handleUserAlreadyExists(UserAlreadyExistsException ex, HttpServletRequest request) {

        return new ResponseEntity<>(RequestUtils.getErrorResponse(request, HttpStatus.CONFLICT, ex), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(CertificateNotFoundException.class)
    public ResponseEntity<Response> handleCertificationNotFound(CertificateNotFoundException ex, HttpServletRequest request) {
        return new ResponseEntity<>(RequestUtils.getErrorResponse(request, HttpStatus.NOT_FOUND, ex), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<Response> handleInvalidToken(InvalidTokenException ex, HttpServletRequest request) {
        return new ResponseEntity<>(RequestUtils.getErrorResponse(request, HttpStatus.UNAUTHORIZED, ex), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(TokenNotFoundException.class)
    public ResponseEntity<Response> handleTokenNotFound(TokenNotFoundException ex, HttpServletRequest request) {
        return new ResponseEntity<>(RequestUtils.getErrorResponse(request, HttpStatus.NOT_FOUND, ex), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(LoginFailedException.class)
    public ResponseEntity<Response> handleLoginFailedException(LoginFailedException ex, HttpServletRequest request) {
        return new ResponseEntity<>(RequestUtils.getErrorResponse(request, HttpStatus.UNAUTHORIZED, ex), HttpStatus.UNAUTHORIZED);
    }


}

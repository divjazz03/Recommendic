package com.divjazz.recommendic;

import com.divjazz.recommendic.consultation.exception.ConsultationAlreadyStartedException;
import com.divjazz.recommendic.exception.EntityNotFoundException;
import com.divjazz.recommendic.security.exception.AuthenticationException;
import com.divjazz.recommendic.security.exception.InvalidTokenException;
import com.divjazz.recommendic.security.exception.LoginFailedException;
import com.divjazz.recommendic.user.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

import javax.security.auth.login.CredentialExpiredException;
import java.time.LocalDateTime;
import java.util.List;

import static com.divjazz.recommendic.RequestUtils.*;
import static com.divjazz.recommendic.RequestUtils.getErrorResponse;

@RestControllerAdvice
public class GlobalControllerExceptionAdvice {

    public record ValidationErrorResponse(String message, List<FieldError> errors){
    }
    public record FieldError(String field, String error){}


    @ExceptionHandler(ConsultationAlreadyStartedException.class)
    public ResponseEntity<Response<String>> handleConsultationAlreadyStarted(ConsultationAlreadyStartedException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(getErrorResponse(HttpStatus.NOT_FOUND, ex));
    }

    @ExceptionHandler(ConfirmationTokenExpiredException.class)
    public ResponseEntity<Response<String>> handleConfirmationTokenExpired(ConfirmationTokenExpiredException ex) {
        return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(getErrorResponse(HttpStatus.EXPECTATION_FAILED,ex));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Response<ValidationErrorResponse>> handleArgumentNotValid(MethodArgumentNotValidException ex) {
        var bindingResult = ex.getBindingResult();

        List<FieldError> fieldErrors = bindingResult.getFieldErrors().stream()
                .map(fieldError -> new FieldError(fieldError.getField(), fieldError.getDefaultMessage()))
                .toList();
        var response = new Response<>(
                LocalDateTime.now().toString(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST,
                "Invalid Field",
                "Invalid Field",
                new ValidationErrorResponse("Validation failed for one or more fields.", fieldErrors)
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(NoSuchMedicalCategory.class)
    public ResponseEntity<Response<String>> handleInvalidMedicalCategory(NoSuchMedicalCategory ex) {
        return new ResponseEntity<>(getErrorResponse(HttpStatus.BAD_REQUEST, ex), HttpStatus.BAD_REQUEST);
    }



    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<Response<String>> handleUserAlreadyExists(UserAlreadyExistsException ex) {

        return new ResponseEntity<>(getErrorResponse(HttpStatus.CONFLICT, ex), HttpStatus.CONFLICT);
    }


    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<Response<String>> handleInvalidToken(InvalidTokenException ex) {
        return new ResponseEntity<>(getErrorResponse(HttpStatus.UNAUTHORIZED, ex), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(LoginFailedException.class)
    public ResponseEntity<Response<String>> handleLoginFailed(LoginFailedException ex) {
        return new ResponseEntity<>(getErrorResponse(HttpStatus.UNAUTHORIZED, ex), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<Response<String>> handleHttpClientError(HttpClientErrorException ex) {
        return new ResponseEntity<>(getResponse(ex.getResponseBodyAsString(), "failed", ex.getStatusCode()),
                ex.getStatusCode());
    }
    @ExceptionHandler(CredentialExpiredException.class)
    public ResponseEntity<Response<String>> handleCredentialExpired(CredentialExpiredException ex) {
        return new ResponseEntity<>(getErrorResponse(HttpStatus.UNAUTHORIZED,ex), HttpStatus.UNAUTHORIZED);
    }
    @ExceptionHandler(LockedException.class)
    public ResponseEntity<Response<String>> handleAccountLocked(LockedException ex) {
        return new ResponseEntity<>(getErrorResponse(HttpStatus.UNAUTHORIZED,ex), HttpStatus.UNAUTHORIZED);
    }
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Response<String>> handleAuthentication(AuthenticationException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(getErrorResponse(HttpStatus.UNAUTHORIZED, ex));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Response<String>> handleEntityNotFound(EntityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(getErrorResponse(HttpStatus.NOT_FOUND, ex));
    }


}

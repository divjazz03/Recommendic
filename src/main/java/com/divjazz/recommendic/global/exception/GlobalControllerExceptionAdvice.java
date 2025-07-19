package com.divjazz.recommendic.global.exception;

import com.divjazz.recommendic.consultation.exception.ConsultationStartedBeforeAppointmentException;
import com.divjazz.recommendic.global.Response;
import com.divjazz.recommendic.consultation.exception.ConsultationAlreadyStartedException;
import com.divjazz.recommendic.security.exception.AuthenticationException;
import com.divjazz.recommendic.security.exception.LoginFailedException;
import com.divjazz.recommendic.user.exception.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.transaction.TransactionException;

import javax.security.auth.login.CredentialExpiredException;
import java.time.LocalDateTime;
import java.util.List;

import static com.divjazz.recommendic.global.RequestUtils.*;
import static com.divjazz.recommendic.global.RequestUtils.getErrorResponse;

@RestControllerAdvice
public class GlobalControllerExceptionAdvice {

    private static final Logger log = LoggerFactory.getLogger(GlobalControllerExceptionAdvice.class);

    public record ValidationErrorResponse(String message, List<FieldError> errors){
    }
    public record FieldError(String field, String error){}

    @ExceptionHandler(TransactionException.class)
    public ResponseEntity<Response<String>> handleTransactionException(TransactionException ex) {
        log.error(ex.getMessage(), ex);
        return ResponseEntity.internalServerError().body(new Response<>(
                LocalDateTime.now().toString(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Something happened on our end and we are hard at work to fix it" ,
                null,
                "Something happened on our end and we are hard at work to fix it"));
    }

    @ExceptionHandler(ConsultationStartedBeforeAppointmentException.class)
    public ResponseEntity<Response<String>> handleConsultationStartedBeforeAppointedTime(
            ConsultationStartedBeforeAppointmentException ex
    ) {
        return ResponseEntity.badRequest().body(getErrorResponse(HttpStatus.BAD_REQUEST,ex,ex.getMessage()));
    }


    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<Response<Object>> handleAccountDisabled(DisabledException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(getErrorResponse(HttpStatus.UNAUTHORIZED,ex,null));
    }

    @ExceptionHandler(ConsultationAlreadyStartedException.class)
    public ResponseEntity<Response<Object>> handleConsultationAlreadyStarted(ConsultationAlreadyStartedException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(getErrorResponse(HttpStatus.CONFLICT, ex,null));
    }

    @ExceptionHandler(ConfirmationTokenExpiredException.class)
    public ResponseEntity<Response<Object>> handleConfirmationTokenExpired(ConfirmationTokenExpiredException ex) {
        return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(getErrorResponse(HttpStatus.EXPECTATION_FAILED,ex,null));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Response<ValidationErrorResponse>> handleArgumentNotValid(MethodArgumentNotValidException ex) {
        var bindingResult = ex.getBindingResult();

        List<FieldError> fieldErrors = bindingResult.getFieldErrors().stream()
                .map(fieldError -> new FieldError(fieldError.getField(), fieldError.getDefaultMessage()))
                .toList();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(getErrorResponse(HttpStatus.BAD_REQUEST,ex, new ValidationErrorResponse("Validation failed for one or more fields.", fieldErrors)));
    }

    @ExceptionHandler(NoSuchMedicalCategory.class)
    public ResponseEntity<Response<Object>> handleInvalidMedicalCategory(NoSuchMedicalCategory ex) {
        return new ResponseEntity<>(getErrorResponse(HttpStatus.BAD_REQUEST, ex, null), HttpStatus.BAD_REQUEST);
    }



    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<Response<String>> handleUserAlreadyExists(UserAlreadyExistsException ex) {

        return new ResponseEntity<>(getErrorResponse(HttpStatus.CONFLICT, ex, null), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(LoginFailedException.class)
    public ResponseEntity<Response<String>> handleLoginFailed(LoginFailedException ex) {
        return new ResponseEntity<>(getErrorResponse(HttpStatus.UNAUTHORIZED, ex, null), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<Response<String>> handleHttpClientError(HttpClientErrorException ex) {
        return new ResponseEntity<>(getResponse(ex.getResponseBodyAsString(), ex.getStatusCode()),
                ex.getStatusCode());
    }
    @ExceptionHandler(CredentialExpiredException.class)
    public ResponseEntity<Response<String>> handleCredentialExpired(CredentialExpiredException ex) {
        return new ResponseEntity<>(getErrorResponse(HttpStatus.UNAUTHORIZED,ex, null), HttpStatus.UNAUTHORIZED);
    }
    @ExceptionHandler(LockedException.class)
    public ResponseEntity<Response<String>> handleAccountLocked(LockedException ex) {
        return new ResponseEntity<>(getErrorResponse(HttpStatus.UNAUTHORIZED,ex, null), HttpStatus.UNAUTHORIZED);
    }
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Response<String>> handleAuthentication(AuthenticationException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(getErrorResponse(HttpStatus.UNAUTHORIZED, ex, null));
    }
    @ExceptionHandler(org.springframework.security.core.AuthenticationException.class)
    public ResponseEntity<Response<String>> handleGeneralAuthenticationError(org.springframework.security.core.AuthenticationException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(getErrorResponse(HttpStatus.UNAUTHORIZED, ex, null));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Response<String>> handleEntityNotFound(EntityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(getErrorResponse(HttpStatus.NOT_FOUND, ex, null));
    }


}

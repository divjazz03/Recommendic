package com.divjazz.recommendic.global.exception;

import com.divjazz.recommendic.appointment.exception.AppointmentBookedException;
import com.divjazz.recommendic.consultation.exception.ConsultationStartedBeforeAppointmentException;
import com.divjazz.recommendic.global.Response;
import com.divjazz.recommendic.consultation.exception.ConsultationAlreadyStartedException;
import com.divjazz.recommendic.security.exception.AuthenticationException;
import com.divjazz.recommendic.security.exception.LoginFailedException;
import com.divjazz.recommendic.user.exception.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.method.ParameterValidationResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.transaction.TransactionException;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import javax.security.auth.login.CredentialExpiredException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.divjazz.recommendic.global.RequestUtils.*;
import static com.divjazz.recommendic.global.RequestUtils.getErrorResponse;

@RestControllerAdvice
public class GlobalControllerExceptionAdvice {

    private static final Logger log = LoggerFactory.getLogger(GlobalControllerExceptionAdvice.class);

    public record ValidationErrorResponse(String message, List<FieldError> errors){
    }
    public record FieldError(String field, String error){}

    @ExceptionHandler(AuthorizationException.class)
    public ProblemDetail handleAuthorizationDenied(AuthorizationException e) {
        return getErrorResponse(HttpStatus.FORBIDDEN,
                        e,
                        "You are not authorized to perform this action");
    }
    @ExceptionHandler(AuthorizationDeniedException.class)
    public ProblemDetail handleAuthorizationDenied(AuthorizationDeniedException e) {
        log.error(e.getMessage());
        return getErrorResponse(HttpStatus.FORBIDDEN,
                        e);
    }

    @ExceptionHandler(TransactionException.class)
    public ProblemDetail handleTransactionException(TransactionException ex) {
        log.error(ex.getMessage(), ex);
        return getErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex);
    }

    @ExceptionHandler(ConsultationStartedBeforeAppointmentException.class)
    public ProblemDetail handleConsultationStartedBeforeAppointedTime(
            ConsultationStartedBeforeAppointmentException ex
    ) {
        return 
                getErrorResponse(HttpStatus.BAD_REQUEST,ex);
    }


    @ExceptionHandler(DisabledException.class)
    public ProblemDetail handleAccountDisabled(DisabledException ex) {
        return getErrorResponse(HttpStatus.UNAUTHORIZED,
                ex,
                "Your account is disabled, please confirm your email");
    }

    @ExceptionHandler(ConsultationAlreadyStartedException.class)
    public ProblemDetail handleConsultationAlreadyStarted(ConsultationAlreadyStartedException ex) {
        return getErrorResponse(HttpStatus.CONFLICT, ex);
    }

    @ExceptionHandler(ConfirmationTokenExpiredException.class)
    public ProblemDetail handleConfirmationTokenExpired(ConfirmationTokenExpiredException ex) {
        return getErrorResponse(HttpStatus.EXPECTATION_FAILED,ex);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleArgumentNotValid(MethodArgumentNotValidException ex) {
        var bindingResult = ex.getBindingResult();

        List<FieldError> fieldErrors = bindingResult.getFieldErrors().stream()
                .map(fieldError -> new FieldError(fieldError.getField(), fieldError.getDefaultMessage()))
                .toList();
        var problem = ProblemDetail.forStatusAndDetail(ex.getStatusCode(), "Validation failed");
        problem.setProperty("data", fieldErrors);
        return problem;
    }
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ProblemDetail handleHandlerMethodValidationException(HandlerMethodValidationException ex) {
        List<ParameterValidationResult> results = ex.getParameterValidationResults();
        return getErrorResponse(HttpStatus.BAD_REQUEST,
                ex,
                results.stream()
                        .flatMap(
                                result -> result.getResolvableErrors().stream()
                                        .map(MessageSourceResolvable::getDefaultMessage)
                        )
                        .toList()
                );
    }

    @ExceptionHandler(NoSuchMedicalCategory.class)
    public ProblemDetail handleInvalidMedicalCategory(NoSuchMedicalCategory ex) {
        return getErrorResponse(HttpStatus.BAD_REQUEST, ex);
    }



    @ExceptionHandler(UserAlreadyExistsException.class)
    public ProblemDetail handleUserAlreadyExists(UserAlreadyExistsException ex) {

        return getErrorResponse(HttpStatus.CONFLICT, ex);
    }

    @ExceptionHandler(LoginFailedException.class)
    public ProblemDetail handleLoginFailed(LoginFailedException ex) {
        return getErrorResponse(HttpStatus.UNAUTHORIZED, ex);
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ProblemDetail handleHttpClientError(HttpClientErrorException ex) {
        log.error(ex.getMessage(), ex);
        return getErrorResponse(ex.getStatusCode(), ex);
    }
    @ExceptionHandler(CredentialExpiredException.class)
    public ProblemDetail handleCredentialExpired(CredentialExpiredException ex) {
        return getErrorResponse(HttpStatus.UNAUTHORIZED,ex);
    }
    @ExceptionHandler(LockedException.class)
    public ProblemDetail handleAccountLocked(LockedException ex) {
        return getErrorResponse(HttpStatus.UNAUTHORIZED,ex);
    }
    @ExceptionHandler(AuthenticationException.class)
    public ProblemDetail handleAuthentication(AuthenticationException ex) {
        return getErrorResponse(HttpStatus.UNAUTHORIZED, ex);
    }
    @ExceptionHandler(org.springframework.security.core.AuthenticationException.class)
    public ProblemDetail handleGeneralAuthenticationError(org.springframework.security.core.AuthenticationException ex) {
        return getErrorResponse(HttpStatus.UNAUTHORIZED, ex);
    }
    @ExceptionHandler(EntityNotFoundException.class)
    public ProblemDetail handleEntityNotFound(EntityNotFoundException ex) {
        return getErrorResponse(HttpStatus.NOT_FOUND, ex);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleGeneralArgumentException(IllegalArgumentException e) {
        log.error(e.getMessage(), e);
        return getErrorResponse(HttpStatus.BAD_REQUEST, e);
    }
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        return getErrorResponse(HttpStatus.BAD_REQUEST, ex);
    }
    @ExceptionHandler(AppointmentBookedException.class)
    public ProblemDetail handleAppointmentBookedException(AppointmentBookedException ex) {
        return getErrorResponse(HttpStatus.BAD_REQUEST, ex);
    }
    @ExceptionHandler(AppBadRequestException.class)
    public ProblemDetail handleAppBadRequestException(AppBadRequestException ex) {
        return getErrorResponse(HttpStatus.BAD_REQUEST, ex);
    }


}

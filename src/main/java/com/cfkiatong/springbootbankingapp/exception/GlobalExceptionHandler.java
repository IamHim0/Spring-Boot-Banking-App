package com.cfkiatong.springbootbankingapp.exception;

import com.cfkiatong.springbootbankingapp.exception.business.BusinessException;
import com.cfkiatong.springbootbankingapp.exception.errorbody.ApiError;
import com.cfkiatong.springbootbankingapp.exception.errorbody.FieldValidationError;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.*;

@ControllerAdvice
public class GlobalExceptionHandler {

    private ApiError buildApiError(HttpStatus status, String message, String path, List<FieldValidationError> errors) {
        return new ApiError(LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                path,
                errors);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleHttpMessageNotReadableException(HttpMessageNotReadableException httpMessageNotReadableException, HttpServletRequest request) {
        return ResponseEntity.badRequest().body(buildApiError(HttpStatus.BAD_REQUEST,
                httpMessageNotReadableException.getMessage(),
                request.getRequestURI(),
                null));
    }

    //Handles validation errors from @Valid or @Validated
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> newHandleValidationExceptions(MethodArgumentNotValidException validationExceptions, HttpServletRequest request) {
        List<FieldValidationError> validationErrors = new ArrayList<>();

        validationExceptions.getBindingResult().getAllErrors().forEach((error) -> {

            FieldValidationError validationError = new FieldValidationError();

            validationError.setField(((FieldError) error).getField());
            validationError.setMessage(error.getDefaultMessage());
            validationError.setRejectedValue(((FieldError) error).getRejectedValue());
            validationErrors.add(validationError);
        });

        return ResponseEntity.badRequest().body(buildApiError(HttpStatus.BAD_REQUEST,
                validationExceptions.getMessage(),
                request.getRequestURI(),
                validationErrors));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiError> handleBusinessException(BusinessException businessException, HttpServletRequest request) {
        Class<?> businessExceptionClass = businessException.getClass();

        HttpStatus status = businessException.getStatus();

        return ResponseEntity.status(status).body(buildApiError(status,
                businessException.getMessage(),
                request.getRequestURI(),
                null));
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ApiError> handleUnauthorizedException(ForbiddenException forbiddenException, HttpServletRequest request) {
        Class<?> unauthorizedExceptionClass = forbiddenException.getClass();

        HttpStatus status = forbiddenException.getStatus();

        return ResponseEntity.status(status).body(buildApiError(status,
                forbiddenException.getMessage(),
                request.getRequestURI(),
                null));
    }

    //SPRINGBOOT EXCEPTIONS HANDLER
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiError> handleBadCredentialsException(BadCredentialsException badCredentialsException, HttpServletRequest request) {
        Class<?> badCredentialsExceptionClass = badCredentialsException.getClass();

        HttpStatus status = HttpStatus.UNAUTHORIZED;

        String message = "Incorrect username or password.";

        return ResponseEntity.status(status).body(buildApiError(status,
                message,
                request.getRequestURI(),
                null));
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<ApiError> handleLockedException(LockedException lockedException, HttpServletRequest request) {
        Class<?> lockedExceptionClass = lockedException.getClass();

        HttpStatus status = HttpStatus.UNAUTHORIZED;

        String message = "User account is locked, please contact an administrator or try again later.";

        return ResponseEntity.status(status).body(buildApiError(status,
                message,
                request.getRequestURI(),
                null));
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ApiError> handleDisabledException(DisabledException disabledException, HttpServletRequest request) {
        Class<?> disabledExceptionClass = disabledException.getClass();

        HttpStatus status = HttpStatus.FORBIDDEN;

        String message = "User account is disabled, please contact an administrator.";

        return ResponseEntity.status(status).body(buildApiError(status,
                message,
                request.getRequestURI(),
                null));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDeniedException(AccessDeniedException accessDeniedException, HttpServletRequest request) {
        Class<?> accessDeniedExceptionClass = accessDeniedException.getClass();

        HttpStatus status = HttpStatus.FORBIDDEN;

        String message = "You are not authorized to perform this transaction";

        return ResponseEntity.status(status).body(buildApiError(status,
                message,
                request.getRequestURI(),
                null));
    }

}
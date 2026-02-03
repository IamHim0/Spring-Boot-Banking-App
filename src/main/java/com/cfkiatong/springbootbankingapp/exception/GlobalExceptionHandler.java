package com.cfkiatong.springbootbankingapp.exception;

import com.cfkiatong.springbootbankingapp.exception.business.BusinessException;
import com.cfkiatong.springbootbankingapp.exception.errorbody.ApiError;
import com.cfkiatong.springbootbankingapp.exception.errorbody.FieldValidationError;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.accept.ApiVersionResolver;
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
        return new ResponseEntity<>(
                buildApiError(HttpStatus.BAD_REQUEST,
                        httpMessageNotReadableException.getMessage(),
                        request.getRequestURI(),
                        null),
                HttpStatus.BAD_REQUEST);
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

        return new ResponseEntity<>(buildApiError(HttpStatus.BAD_REQUEST,
                validationExceptions.getMessage(),
                request.getRequestURI(),
                validationErrors), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiError> handleBusinessException(BusinessException businessException, HttpServletRequest request) {
        Class<?> businessExceptionClass = businessException.getClass();

        HttpStatus status = HttpStatus.BAD_REQUEST;

        status = switch (businessExceptionClass.getSimpleName()) {
            case "InsufficientBalanceException" -> HttpStatus.UNPROCESSABLE_ENTITY;
            case "AccountNotFoundException" -> HttpStatus.NOT_FOUND;
            case "UsernameUnavailableException" -> HttpStatus.CONFLICT;
            case "NoFieldUpdatedException" -> HttpStatus.UNPROCESSABLE_ENTITY;
            default -> status;
        };

        return new ResponseEntity<>(
                buildApiError(status,
                        businessException.getMessage(),
                        request.getRequestURI(),
                        null),
                status);
    }

//    @ExceptionHandler(IllegalArgumentException.class)
//    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException argumentException) {
//        Map<String, String> error = new HashMap<>();
//
//        error.put("error: ", argumentException.getMessage());
//
//        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
//    }

}

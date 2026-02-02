package com.cfkiatong.springbootbankingapp.exception;

import com.cfkiatong.springbootbankingapp.exception.business.AccountNotFoundException;
import com.cfkiatong.springbootbankingapp.exception.business.BusinessException;
import com.cfkiatong.springbootbankingapp.exception.business.InsufficientBalanceException;
import com.cfkiatong.springbootbankingapp.exception.business.UsernameUnavailableException;
import com.cfkiatong.springbootbankingapp.exception.errorbody.ApiError;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import tools.jackson.databind.json.JsonMapper;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private final JsonMapper.Builder builder;

    public GlobalExceptionHandler(JsonMapper.Builder builder) {
        this.builder = builder;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleHttpMessageNotReadableException(HttpMessageNotReadableException httpMessageNotReadableException, HttpServletRequest request) {
        ApiError apiError = new ApiError(LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                httpMessageNotReadableException.getMessage(),
                request.getRequestURI(),
                null);

        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    //Handles validation errors from @Valid or @Validated
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException validationExceptions) {
        Map<String, String> errors = new HashMap<>();

        validationExceptions.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> newHandleValidationExceptions(MethodArgumentNotValidException validException, HttpServletRequest request) {
        ApiError apiError = new ApiError(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                validException.getMessage(),
                request.getRequestURI(),
                null
        );

        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
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

        ApiError apiError = new ApiError(LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                businessException.getMessage(),
                request.getRequestURI(),
                null);

        return new ResponseEntity<>(apiError, status);
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

package com.cfkiatong.springbootbankingapp.exception.business;

import org.springframework.http.HttpStatus;

public abstract class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }

    public abstract HttpStatus getStatus();

}

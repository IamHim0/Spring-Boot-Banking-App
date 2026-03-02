package com.cfkiatong.springbootbankingapp.exception.business;

import org.springframework.http.HttpStatus;

public class UpdateUserStatusException extends BusinessException {
    public UpdateUserStatusException(String message) {
        super(message);
    }

    public HttpStatus getStatus() {
        return HttpStatus.CONFLICT;
    }
}

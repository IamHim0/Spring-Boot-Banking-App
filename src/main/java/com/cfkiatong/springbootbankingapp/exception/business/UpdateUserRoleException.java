package com.cfkiatong.springbootbankingapp.exception.business;

import org.springframework.http.HttpStatus;

public class UpdateUserRoleException extends BusinessException {
    public UpdateUserRoleException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.UNPROCESSABLE_CONTENT;
    }
}

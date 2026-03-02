package com.cfkiatong.springbootbankingapp.exception.business;

import org.springframework.http.HttpStatus;

public class UserInactiveException extends BusinessException {
    public UserInactiveException(String username) {

        super("User account, '" + username + "' is currently inactive.");
    }

    public HttpStatus getStatus() {
        return HttpStatus.FORBIDDEN;
    }
}

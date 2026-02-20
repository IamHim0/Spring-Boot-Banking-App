package com.cfkiatong.springbootbankingapp.exception.business;

import org.springframework.http.HttpStatus;

public class UserNotFoundException extends BusinessException {
    public UserNotFoundException(String username) {

        super("The user with username '" + username + "' does not exist");
    }

    public HttpStatus getStatus() {
        return HttpStatus.NOT_FOUND;
    }
}

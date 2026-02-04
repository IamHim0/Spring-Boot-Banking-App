package com.cfkiatong.springbootbankingapp.exception.business;

import org.springframework.http.HttpStatus;

public class UsernameUnavailableException extends BusinessException {

    public UsernameUnavailableException(String username) {
        super("The username '" + username + "' is unavailable.");
    }

    public HttpStatus getStatus() {
        return HttpStatus.CONFLICT;
    }

}

package com.cfkiatong.springbootbankingapp.exception.business;

public class UsernameUnavailableException extends BusinessException {

    public UsernameUnavailableException(String username) {
        super("The username '" + "' is unavailable.");
    }
}

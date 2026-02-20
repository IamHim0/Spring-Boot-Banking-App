package com.cfkiatong.springbootbankingapp.exception.business;

import org.springframework.http.HttpStatus;

import java.util.UUID;

public class AccountNotFoundException extends BusinessException {
    public AccountNotFoundException(String username) {
        super("The account with username '" + username + "' does not exist");
    }

    public AccountNotFoundException(UUID id) {
        super("The account with accountId '" + id + "' does not exist");
    }

    public HttpStatus getStatus() {
        return HttpStatus.NOT_FOUND;
    }
}

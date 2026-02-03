package com.cfkiatong.springbootbankingapp.exception.business;

import java.util.UUID;

public class AccountNotFoundException extends BusinessException {
    public AccountNotFoundException(String username) {
        super("The account with username '" + username + "' does not exist");
    }

    public AccountNotFoundException(UUID id) {
        super("The account with username '" + id + "' does not exist");
    }
}

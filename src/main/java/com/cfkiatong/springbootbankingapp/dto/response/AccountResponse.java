package com.cfkiatong.springbootbankingapp.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public class AccountResponse {

    private final UUID accountId;
    private final String accountOwner;
    private final BigDecimal balance;

    public AccountResponse(UUID accountId, String accountOwner, BigDecimal balance) {
        this.accountId = accountId;
        this.accountOwner = accountOwner;
        this.balance = balance;
    }

    public UUID getId() {
        return accountId;
    }

    public String getAccountOwner() {
        return accountOwner;
    }

    public BigDecimal getBalance() {
        return balance;
    }

}
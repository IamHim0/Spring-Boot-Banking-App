package com.cfkiatong.springbootbankingapp.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public class GetAccountResponse {

    private UUID id;
    private String accountOwner;
    private BigDecimal balance;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getAccountOwner() {
        return accountOwner;
    }

    public void setAccountOwner(String accountOwner) {
        this.accountOwner = accountOwner;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

}

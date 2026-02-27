package com.cfkiatong.springbootbankingapp.dto.response;

import java.math.BigDecimal;

public class GetBalanceResponse {

    private final BigDecimal balance;

    public GetBalanceResponse(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
    }
}

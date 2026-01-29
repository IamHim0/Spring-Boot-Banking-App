package com.cfkiatong.springbootbankingapp.dto;

import java.math.BigDecimal;

public class DepositRequest {
    private BigDecimal deposit;

    public BigDecimal getDeposit() {
        return deposit;
    }

    public void setDeposit(BigDecimal deposit) {
        this.deposit = deposit;
    }
}

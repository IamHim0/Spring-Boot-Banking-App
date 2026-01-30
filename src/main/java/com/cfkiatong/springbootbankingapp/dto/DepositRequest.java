package com.cfkiatong.springbootbankingapp.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class DepositRequest {

    @NotNull
    @DecimalMin("0.1")
    private BigDecimal deposit;

    public BigDecimal getDeposit() {
        return deposit;
    }

    public void setDeposit(BigDecimal deposit) {
        this.deposit = deposit;
    }
}

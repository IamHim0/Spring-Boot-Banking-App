package com.cfkiatong.springbootbankingapp.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class ViewBalanceResponse {

    public ViewBalanceResponse(BigDecimal balance) {}
}
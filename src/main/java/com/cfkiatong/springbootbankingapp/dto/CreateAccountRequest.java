package com.cfkiatong.springbootbankingapp.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public class CreateAccountRequest {

    @NotNull(message = "initial deposit cannot be empty ")
    @DecimalMin(value = "1000", message = "initial deposit must be at least ₱ 1000")
    private BigDecimal initialDeposit;
    private AccountType accountType;
    private UUID ownerId;

    public CreateAccountRequest(BigDecimal initialDeposit, AccountType accountType, UUID ownerId) {
        this.initialDeposit = initialDeposit;
        this.accountType = accountType;
        this.ownerId = ownerId;
    }

    public BigDecimal getInitialDeposit() {
        return initialDeposit;
    }

    public void setInitialDeposit(BigDecimal initialDeposit) {
        this.initialDeposit = initialDeposit;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(UUID ownerId) {
        this.ownerId = ownerId;
    }
}

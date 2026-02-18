package com.cfkiatong.springbootbankingapp.dto;

import jakarta.validation.constraints.NotBlank;

public class ChangeAccountTypeRequest {

    @NotBlank(message = "new account type cannot be empty")
    private AccountType accountType;

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

}
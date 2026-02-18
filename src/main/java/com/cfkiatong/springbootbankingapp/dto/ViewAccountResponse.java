package com.cfkiatong.springbootbankingapp.dto;

import com.cfkiatong.springbootbankingapp.entity.Account;

import java.math.BigDecimal;
import java.util.UUID;

public class ViewAccountResponse {

    private AccountType accountType;
    private String ownerUsername;
    private BigDecimal balance;
    private UUID accountId;

    public ViewAccountResponse(AccountType accountType, String ownerUsername, BigDecimal balance, UUID accountId) {
        this.accountType = accountType;
        this.ownerUsername = ownerUsername;
        this.balance = balance;
        this.accountId = accountId;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public String getOwnerUsername() {
        return ownerUsername;
    }

    public void setOwnerUsername(String ownerUsername) {
        this.ownerUsername = ownerUsername;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public UUID getAccountId() {
        return accountId;
    }

    public void setAccountId(UUID accountId) {
        this.accountId = accountId;
    }
}

package com.cfkiatong.springbootbankingapp.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private BigDecimal balance;
    @ManyToOne
    @JoinColumn(name = "account_owner_user_id")
    private UserEntity accountOwner;

    protected Account() {

    }

    public Account(UserEntity accountOwner) {
        this.accountOwner = accountOwner;
        this.balance = new BigDecimal("0");
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public UserEntity getAccountOwner() {
        return accountOwner;
    }

    public void setAccountOwner(UserEntity accountOwner) {
        this.accountOwner = accountOwner;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Account account)) return false;
        return Objects.equals(id, account.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}

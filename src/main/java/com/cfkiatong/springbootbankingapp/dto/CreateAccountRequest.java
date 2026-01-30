package com.cfkiatong.springbootbankingapp.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public class CreateAccountRequest {

    @NotBlank(message = "first name must not be empty")
    private String firstName;
    @NotBlank(message = "last name must not be empty")
    private String lastName;
    @NotBlank(message = "username must not be empty")
    private String username;
    @NotBlank(message = "password must not be empty")
    private String password;
    @NotNull(message = "initial deposit cannot be empty ")
    @DecimalMin(value = "1000", message = "initial deposit must be at least ₱ 1000")
    private BigDecimal initialBalance;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public BigDecimal getInitialBalance() {
        return initialBalance;
    }

    public void setInitialBalance(BigDecimal initialBalance) {
        this.initialBalance = initialBalance;
    }

}
